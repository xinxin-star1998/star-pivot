package com.star.pivot.generator.path;

import com.star.pivot.framework.constants.GenConstants;
import com.star.pivot.generator.domain.entity.GenTable;
import com.star.pivot.generator.domain.external.GenPathProfile;
import com.star.pivot.generator.utils.StringUtils;
import com.star.pivot.generator.utils.VelocityUtils;

/**
 * 根据 {@link GenPathProfile} 解析 ZIP 内文件路径
 */
public final class GenPathResolver {

    private static final String JAVA_ROOT = "main/java/";

    private GenPathResolver() {
    }

    public static String packageToPath(String javaPackage) {
        return JAVA_ROOT + javaPackage.replace('.', '/');
    }

    /**
     * 解析 ZIP 条目路径；无 profile 时回退 {@link VelocityUtils#getFileName}
     */
    public static String resolveZipEntryPath(String template, GenTable table, GenPathProfile profile) {
        if (profile == null) {
            return VelocityUtils.getFileName(template, table);
        }
        String className = table.getClassName();
        String businessName = table.getBusinessName();
        String reqBoSuffix = GenConstants.TPL_TREE.equals(table.getTplCategory()) ? "ReqBo" : "ReqPageBo";

        if (template.contains("domain.java.vm") && !template.contains("sub-domain")) {
            return packageToPath(profile.getEntityPackage()) + "/" + className + ".java";
        }
        if (template.contains("sub-domain.java.vm")) {
            return packageToPath(profile.getEntityPackage()) + "/" + table.getSubTable().getClassName() + ".java";
        }
        if (template.contains("reqBo.java.vm")) {
            return packageToPath(profile.getBoPackage()) + "/" + className + reqBoSuffix + ".java";
        }
        if (template.contains("dto.java.vm") && !template.contains("sub-dto")) {
            return packageToPath(profile.getDtoPackage()) + "/" + className + "DTO.java";
        }
        if (template.contains("sub-dto.java.vm")) {
            return packageToPath(profile.getDtoPackage()) + "/" + table.getSubTable().getClassName() + "DTO.java";
        }
        if (template.contains("vo.java.vm") && !template.contains("sub-vo")) {
            return packageToPath(profile.getVoPackage()) + "/" + className + "VO.java";
        }
        if (template.contains("sub-vo.java.vm")) {
            return packageToPath(profile.getVoPackage()) + "/" + table.getSubTable().getClassName() + "VO.java";
        }
        if (template.contains("mapper.java.vm")) {
            return packageToPath(profile.getMapperPackage()) + "/" + className + "Mapper.java";
        }
        if (template.contains("service.java.vm")) {
            return packageToPath(profile.getServicePackage()) + "/I" + className + "Service.java";
        }
        if (template.contains("serviceImpl.java.vm")) {
            return packageToPath(profile.getServiceImplPackage()) + "/" + className + "ServiceImpl.java";
        }
        if (template.contains("controller.java.vm")) {
            return packageToPath(profile.getControllerPackage()) + "/" + className + "Controller.java";
        }
        if (template.contains("mapper.xml.vm")) {
            String xmlBase = trimSlash(profile.getMapperXmlPath());
            return xmlBase + "/" + className + "Mapper.xml";
        }
        if (template.contains("sql.vm")) {
            return businessName + "Menu.sql";
        }
        if (template.contains("api.js.vm") || template.contains("api.ts.vm")) {
            return trimSlash(profile.getApiPath()) + "/" + businessName + ".ts";
        }
        if (template.contains("search.vue.vm")) {
            return trimSlash(profile.resolveVueModulesPath()) + "/" + businessName + "-search.vue";
        }
        if (template.contains("dialog.vue.vm")) {
            return trimSlash(profile.resolveVueModulesPath()) + "/" + businessName + "-dialog.vue";
        }
        if (template.contains("index.vue.vm") || template.contains("index-tree.vue.vm")) {
            if (StringUtils.isEmpty(profile.getVuePagePath())) {
                return VelocityUtils.getFileName(template, table);
            }
            return trimSlash(profile.getVuePagePath()) + "/index.vue";
        }
        return VelocityUtils.getFileName(template, table);
    }

    private static String trimSlash(String path) {
        if (path == null) {
            return "";
        }
        String p = path;
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        while (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }
}
