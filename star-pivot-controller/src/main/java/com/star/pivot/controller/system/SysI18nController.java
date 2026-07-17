package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.utils.MessageUtils;
import com.star.pivot.system.domain.dto.I18nImportDTO;
import com.star.pivot.system.domain.dto.I18nResourceDTO;
import com.star.pivot.system.domain.dto.SysLangDTO;
import com.star.pivot.system.domain.entity.SysLang;
import com.star.pivot.system.domain.vo.I18nCoverageVO;
import com.star.pivot.system.service.interfaces.SysI18nService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 国际化管理
 */
@RestController
@RequestMapping("/system/i18n")
@RequiredArgsConstructor
@Tag(name = "国际化管理", description = "语言与翻译资源管理")
public class SysI18nController {

    private final SysI18nService sysI18nService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * 公开启用语言列表（登录页语言切换等未认证场景）
     */
    @Operation(summary = "启用语言列表")
    @GetMapping("/lang/list")
    public Result<List<SysLang>> listEnabledLangs() {
        return Result.success(sysI18nService.listEnabledLangs());
    }

    @Operation(summary = "全部语言列表（含停用）")
    @PreAuthorize("hasAnyAuthority('system:i18n:query', 'system:i18n:list')")
    @GetMapping("/lang/all")
    public Result<List<SysLang>> listAllLangs() {
        return Result.success(sysI18nService.listAllLangs());
    }

    @Operation(summary = "新增语言")
    @Log(title = "新增语言", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:i18n:edit')")
    @PostMapping("/lang")
    public Result<?> addLang(@Valid @RequestBody SysLangDTO dto) {
        boolean ok = sysI18nService.insertLang(dto);
        return ok
                ? Result.success(MessageUtils.message("i18n.lang.add.success", "新增语言成功"))
                : Result.error(MessageUtils.message("i18n.lang.add.fail", "新增语言失败"));
    }

    @Operation(summary = "修改语言")
    @Log(title = "修改语言", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:i18n:edit')")
    @PutMapping("/lang")
    public Result<?> editLang(@Valid @RequestBody SysLangDTO dto) {
        boolean ok = sysI18nService.updateLang(dto);
        return ok
                ? Result.success(MessageUtils.message("i18n.lang.edit.success", "修改语言成功"))
                : Result.error(MessageUtils.message("i18n.lang.edit.fail", "修改语言失败"));
    }

    @Operation(summary = "启停语言")
    @Log(title = "启停语言", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:i18n:edit')")
    @PutMapping("/lang/{langId}/status")
    public Result<?> updateLangStatus(@PathVariable Long langId, @RequestParam String status) {
        boolean ok = sysI18nService.updateLangStatus(langId, status);
        return ok
                ? Result.success(MessageUtils.message("i18n.lang.status.success", "更新语言状态成功"))
                : Result.error(MessageUtils.message("i18n.lang.status.fail", "更新语言状态失败"));
    }

    @Operation(summary = "查询资源多语言文案")
    @PreAuthorize("hasAnyAuthority('system:i18n:query', 'system:menu:query', 'system:data:query')")
    @GetMapping("/resource")
    public Result<Map<String, String>> getResource(
            @RequestParam String namespace,
            @RequestParam String resourceKey,
            @RequestParam(defaultValue = "menu_name") String fieldName) {
        return Result.success(sysI18nService.getResourceTranslations(namespace, resourceKey, fieldName));
    }

    @Operation(summary = "批量保存资源多语言文案")
    @Log(title = "保存国际化资源", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAnyAuthority('system:i18n:edit', 'system:menu:edit', 'system:data:edit')")
    @PutMapping("/resource")
    public Result<?> saveResource(@Valid @RequestBody I18nResourceDTO dto) {
        sysI18nService.saveResource(dto);
        return Result.success(MessageUtils.message("common.save.success", "保存成功"));
    }

    @Operation(summary = "按命名空间拉取语言包")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/bundle")
    public Result<Map<String, String>> getBundle(
            @RequestParam String namespace,
            @RequestParam String lang) {
        return Result.success(sysI18nService.getBundle(namespace, lang));
    }

    /**
     * 公开拉取 UI 静态文案语言包（登录页等未认证场景）
     */
    @Operation(summary = "公开拉取 UI 语言包")
    @GetMapping("/bundle/ui")
    public Result<Map<String, String>> getUiBundle(@RequestParam String lang) {
        return Result.success(sysI18nService.getBundle("ui", lang));
    }

    @Operation(summary = "翻译覆盖率与缺失列表")
    @PreAuthorize("hasAnyAuthority('system:i18n:query', 'system:i18n:list')")
    @GetMapping("/coverage")
    public Result<I18nCoverageVO> coverage(
            @RequestParam String namespace,
            @RequestParam String lang,
            @RequestParam(required = false) String fieldName) {
        return Result.success(sysI18nService.getCoverage(namespace, lang, fieldName));
    }

    @Operation(summary = "导出语言包 JSON")
    @PreAuthorize("hasAnyAuthority('system:i18n:query', 'system:i18n:list', 'system:i18n:export')")
    @NoResponseWrapper
    @GetMapping("/export")
    public void exportBundle(
            @RequestParam String namespace,
            @RequestParam String lang,
            HttpServletResponse response) throws IOException {
        Map<String, String> bundle = sysI18nService.getBundle(namespace, lang);
        String filename = URLEncoder.encode(
                "i18n-" + namespace + "-" + lang + ".json", StandardCharsets.UTF_8).replace("+", "%20");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), bundle);
    }

    @Operation(summary = "导入语言包")
    @Log(title = "导入国际化资源", businessType = AppConstants.BusinessType.IMPORT)
    @PreAuthorize("hasAnyAuthority('system:i18n:edit', 'system:i18n:import')")
    @PostMapping("/import")
    public Result<?> importBundle(@Valid @RequestBody I18nImportDTO dto) {
        int count = sysI18nService.importTranslations(dto);
        return Result.success(MessageUtils.message("i18n.import.success", "导入成功，共 " + count + " 条", count));
    }
}
