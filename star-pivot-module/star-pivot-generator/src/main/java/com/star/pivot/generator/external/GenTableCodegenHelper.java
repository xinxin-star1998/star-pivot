package com.star.pivot.generator.external;

import com.star.pivot.framework.constants.GenConstants;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.generator.domain.dto.external.ExternalWriteDiffItemVO;
import com.star.pivot.generator.domain.entity.GenTable;
import com.star.pivot.generator.domain.entity.GenTableColumn;
import com.star.pivot.generator.domain.external.ExternalGenScope;
import com.star.pivot.generator.domain.external.GenPathProfile;
import com.star.pivot.generator.path.GenPathResolver;
import com.star.pivot.generator.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 基于 GenTable 草稿渲染 Velocity 模板
 */
public final class GenTableCodegenHelper {

    public static final String DIFF_NEW = "NEW";
    public static final String DIFF_MODIFIED = "MODIFIED";
    public static final String DIFF_UNCHANGED = "UNCHANGED";

    private GenTableCodegenHelper() {
    }

    public static void setPkColumn(GenTable table) {
        List<GenTableColumn> columns = table.getColumns();
        if (StringUtils.isEmpty(columns)) {
            throw new BizException("表字段列表不能为空，表名：" + table.getTableName());
        }
        for (GenTableColumn column : columns) {
            if (column.isPk()) {
                table.setPkColumn(column);
                break;
            }
        }
        if (StringUtils.isNull(table.getPkColumn())) {
            table.setPkColumn(columns.get(0));
        }
    }

    public static Map<String, String> preview(GenTable table, GenPathProfile pathProfile) {
        return preview(table, pathProfile, null, null);
    }

    public static Map<String, String> preview(GenTable table, GenPathProfile pathProfile, ExternalGenScope scope) {
        return preview(table, pathProfile, scope, null);
    }

    public static Map<String, String> preview(GenTable table, GenPathProfile pathProfile, ExternalGenScope scope,
            String templateDir) {
        return renderCodeMap(table, pathProfile, scope, templateDir);
    }

    public static void writeZip(GenTable table, GenPathProfile pathProfile, ZipOutputStream zip) throws IOException {
        writeZip(table, pathProfile, zip, null, null);
    }

    public static void writeZip(GenTable table, GenPathProfile pathProfile, ZipOutputStream zip, ExternalGenScope scope)
            throws IOException {
        writeZip(table, pathProfile, zip, scope, null);
    }

    public static void writeZip(GenTable table, GenPathProfile pathProfile, ZipOutputStream zip, ExternalGenScope scope,
            String templateDir) throws IOException {
        Map<String, String> codeMap = renderCodeMap(table, pathProfile, scope, templateDir);
        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            zip.putNextEntry(new ZipEntry(entry.getKey()));
            IOUtils.write(entry.getValue(), zip, CharsetKit.CHARSET_UTF_8);
            zip.flush();
            zip.closeEntry();
        }
    }

    public static List<String> writeToDisk(GenTable table, GenPathProfile pathProfile, ExternalGenScope scope,
            Path outputRoot, String templateDir) throws IOException {
        return writeToDisk(table, pathProfile, scope, outputRoot, templateDir, null);
    }

    public static List<String> writeToDisk(GenTable table, GenPathProfile pathProfile, ExternalGenScope scope,
            Path outputRoot, String templateDir, WriteDiskFilter filter) throws IOException {
        return writeToDiskOutcome(table, pathProfile, scope, outputRoot, templateDir, filter).writtenFiles();
    }

    public static WriteToDiskOutcome writeToDiskOutcome(GenTable table, GenPathProfile pathProfile,
            ExternalGenScope scope, Path outputRoot, String templateDir, WriteDiskFilter filter) throws IOException {
        Map<String, String> codeMap = renderCodeMap(table, pathProfile, scope, templateDir);
        List<String> written = new ArrayList<>();
        Set<String> selected = filter != null && filter.selectedPaths != null
                ? new HashSet<>(filter.selectedPaths)
                : null;
        boolean doBackup = filter != null && filter.backupBeforeWrite;
        String backupId = null;
        if (doBackup) {
            if (filter != null && StringUtils.isNotEmpty(filter.existingBackupId)) {
                backupId = filter.existingBackupId;
            } else {
                backupId = ExternalWriteBackupHelper.newBackupId();
                if (filter != null) {
                    filter.existingBackupId = backupId;
                }
            }
        }
        int backedUpCount = 0;
        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            if (selected != null && !selected.isEmpty() && !selected.contains(entry.getKey())) {
                continue;
            }
            String relative = entry.getKey().replace('/', File.separatorChar);
            File target = outputRoot.resolve(relative).toFile();
            String newContent = entry.getValue();
            if (filter != null && filter.onlyChanged && target.isFile()) {
                String oldContent = FileUtils.readFileToString(target, CharsetKit.CHARSET_UTF_8);
                if (normalizeContent(oldContent).equals(normalizeContent(newContent))) {
                    continue;
                }
            }
            if (backupId != null && target.isFile()) {
                ExternalWriteBackupHelper.backupFileIfExists(outputRoot, backupId, entry.getKey());
                backedUpCount++;
            }
            FileUtils.forceMkdirParent(target);
            FileUtils.writeStringToFile(target, newContent, CharsetKit.CHARSET_UTF_8);
            written.add(entry.getKey());
        }
        String resultBackupId = backedUpCount > 0 && backupId != null ? backupId : null;
        return new WriteToDiskOutcome(written, resultBackupId, backedUpCount);
    }

    public static List<ExternalWriteDiffItemVO> computeWriteDiff(GenTable table, GenPathProfile pathProfile,
            ExternalGenScope scope, Path outputRoot, String templateDir) throws IOException {
        Map<String, String> codeMap = renderCodeMap(table, pathProfile, scope, templateDir);
        List<ExternalWriteDiffItemVO> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            String relative = entry.getKey().replace('/', File.separatorChar);
            File target = outputRoot.resolve(relative).toFile();
            String newContent = entry.getValue();
            if (!target.isFile()) {
                items.add(ExternalWriteDiffItemVO.builder()
                        .path(entry.getKey())
                        .status(DIFF_NEW)
                        .newContent(newContent)
                        .build());
                continue;
            }
            String oldContent = FileUtils.readFileToString(target, CharsetKit.CHARSET_UTF_8);
            if (normalizeContent(oldContent).equals(normalizeContent(newContent))) {
                items.add(ExternalWriteDiffItemVO.builder()
                        .path(entry.getKey())
                        .status(DIFF_UNCHANGED)
                        .build());
            } else {
                items.add(ExternalWriteDiffItemVO.builder()
                        .path(entry.getKey())
                        .status(DIFF_MODIFIED)
                        .oldContent(oldContent)
                        .newContent(newContent)
                        .build());
            }
        }
        return items;
    }

    public static void stripDiffContents(List<ExternalWriteDiffItemVO> items) {
        for (ExternalWriteDiffItemVO item : items) {
            item.setOldContent(null);
            item.setNewContent(null);
        }
    }

    public static WriteDiskFilter buildWriteFilter(boolean onlyChanged, List<String> selectedPaths,
            boolean backupBeforeWrite) {
        WriteDiskFilter filter = new WriteDiskFilter();
        filter.onlyChanged = onlyChanged;
        filter.selectedPaths = selectedPaths;
        filter.backupBeforeWrite = backupBeforeWrite;
        return filter;
    }

    public static final class WriteToDiskOutcome {
        private final List<String> writtenFiles;
        private final String backupId;
        private final int backedUpCount;

        public WriteToDiskOutcome(List<String> writtenFiles, String backupId, int backedUpCount) {
            this.writtenFiles = writtenFiles;
            this.backupId = backupId;
            this.backedUpCount = backedUpCount;
        }

        public List<String> writtenFiles() {
            return writtenFiles;
        }

        public String backupId() {
            return backupId;
        }

        public int backedUpCount() {
            return backedUpCount;
        }
    }

    public static final class WriteDiskFilter {
        private boolean onlyChanged;
        private List<String> selectedPaths;
        private boolean backupBeforeWrite;
        /** 多表写盘共用同一备份批次 */
        private String existingBackupId;
    }

    private static String normalizeContent(String content) {
        if (content == null) {
            return "";
        }
        return content.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static Map<String, String> renderCodeMap(GenTable table, GenPathProfile pathProfile, ExternalGenScope scope,
            String templateDir) {
        prepareTable(table);
        VelocityInitializer.initVelocity(templateDir);
        VelocityContext context = VelocityUtils.prepareContext(table, pathProfile);
        Map<String, String> dataMap = new LinkedHashMap<>();
        List<String> templates = filterTemplates(
                VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType()), scope);
        for (String template : templates) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            String key = pathProfile != null
                    ? GenPathResolver.resolveZipEntryPath(template, table, pathProfile)
                    : VelocityUtils.getDisplayName(template, table);
            dataMap.put(key, sw.toString());
        }
        return dataMap;
    }

    private static void prepareTable(GenTable table) {
        if (StringUtils.isEmpty(table.getTplCategory())) {
            table.setTplCategory(GenConstants.TPL_CRUD);
        }
        if (StringUtils.isEmpty(table.getTplWebType())) {
            table.setTplWebType("art-design-pro");
        }
        setPkColumn(table);
    }

    public static List<String> filterTemplates(List<String> templates, ExternalGenScope scope) {
        if (scope == null || scope.isAllEnabled()) {
            return templates;
        }
        List<String> filtered = new ArrayList<>();
        for (String template : templates) {
            if (matchesScope(template, scope)) {
                filtered.add(template);
            }
        }
        return filtered;
    }

    private static boolean matchesScope(String template, ExternalGenScope scope) {
        if (template.contains("sql.vm")) {
            return scope.isGenSql();
        }
        if (template.contains("vm/java/") || template.contains("vm/xml/")) {
            return scope.isGenBackend();
        }
        if (template.contains("vm/vue/") || template.contains("api.js.vm")) {
            return scope.isGenFrontend();
        }
        return true;
    }
}
