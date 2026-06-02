package com.star.pivot.generator.domain.external;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.generator.utils.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 代码生成路径配置（会话级，不落库）
 */
@Data
public class GenPathProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$");
    private static final Pattern SAFE_PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9_./\\-]+$");

    @NotBlank(message = "基础包名不能为空")
    private String basePackage;

    private String entityPackage;
    private String dtoPackage;
    private String voPackage;
    private String boPackage;

    private String mapperPackage;
    private String servicePackage;
    private String serviceImplPackage;
    private String controllerPackage;

    /** ZIP 内 Mapper XML 目录，如 main/resources/mapper/mall */
    private String mapperXmlPath;

    /** ZIP 内 API 目录，如 star-pivot-ui/src/api/mall */
    private String apiPath;

    /** ZIP 内页面目录，如 star-pivot-ui/src/views/mall/brand */
    private String vuePagePath;

    /** ZIP 内子组件目录，默认可由 {@link #resolveVueModulesPath()} 推导 */
    private String vueModulesPath;

    /**
     * 根据基础包与模块名填充默认子包路径（未填写的项）
     */
    public void fillDefaults(String moduleName) {
        if (StringUtils.isEmpty(basePackage)) {
            throw new BizException("基础包名 basePackage 不能为空");
        }
        String module = StringUtils.isNotEmpty(moduleName) ? moduleName : getModuleNameFromBase();
        if (StringUtils.isEmpty(entityPackage)) {
            entityPackage = basePackage + ".domain.entity";
        }
        if (StringUtils.isEmpty(dtoPackage)) {
            dtoPackage = basePackage + ".domain.dto";
        }
        if (StringUtils.isEmpty(voPackage)) {
            voPackage = basePackage + ".domain.bo";
        }
        if (StringUtils.isEmpty(boPackage)) {
            boPackage = basePackage + ".domain.bo";
        }
        if (StringUtils.isEmpty(mapperPackage)) {
            mapperPackage = basePackage + ".mapper";
        }
        if (StringUtils.isEmpty(servicePackage)) {
            servicePackage = basePackage + ".service";
        }
        if (StringUtils.isEmpty(serviceImplPackage)) {
            serviceImplPackage = basePackage + ".service.impl";
        }
        if (StringUtils.isEmpty(controllerPackage)) {
            controllerPackage = basePackage + ".controller";
        }
        if (StringUtils.isEmpty(mapperXmlPath)) {
            mapperXmlPath = "main/resources/mapper/" + module;
        }
        if (StringUtils.isEmpty(apiPath)) {
            apiPath = "star-pivot-ui/src/api/" + module;
        }
    }

    public String resolveVueModulesPath() {
        if (StringUtils.isNotEmpty(vueModulesPath)) {
            return vueModulesPath;
        }
        if (StringUtils.isNotEmpty(vuePagePath)) {
            return vuePagePath + "/modules";
        }
        return null;
    }

    public GenPathProfile copy() {
        GenPathProfile p = new GenPathProfile();
        p.setBasePackage(basePackage);
        p.setEntityPackage(entityPackage);
        p.setDtoPackage(dtoPackage);
        p.setVoPackage(voPackage);
        p.setBoPackage(boPackage);
        p.setMapperPackage(mapperPackage);
        p.setServicePackage(servicePackage);
        p.setServiceImplPackage(serviceImplPackage);
        p.setControllerPackage(controllerPackage);
        p.setMapperXmlPath(mapperXmlPath);
        p.setApiPath(apiPath);
        p.setVuePagePath(vuePagePath);
        p.setVueModulesPath(vueModulesPath);
        return p;
    }

    public void validate() {
        if (StringUtils.isEmpty(basePackage)) {
            throw new BizException("基础包名不能为空");
        }
        validatePackage(basePackage, "basePackage");
        validatePackage(entityPackage, "entityPackage");
        validatePackage(dtoPackage, "dtoPackage");
        validatePackage(voPackage, "voPackage");
        validatePackage(boPackage, "boPackage");
        validatePackage(mapperPackage, "mapperPackage");
        validatePackage(servicePackage, "servicePackage");
        validatePackage(serviceImplPackage, "serviceImplPackage");
        validatePackage(controllerPackage, "controllerPackage");
        validateZipPath(mapperXmlPath, "mapperXmlPath");
        validateZipPath(apiPath, "apiPath");
        if (StringUtils.isNotEmpty(vuePagePath)) {
            validateZipPath(vuePagePath, "vuePagePath");
        }
        if (StringUtils.isNotEmpty(vueModulesPath)) {
            validateZipPath(vueModulesPath, "vueModulesPath");
        }
    }

    private String getModuleNameFromBase() {
        int idx = basePackage.lastIndexOf('.');
        return idx >= 0 ? basePackage.substring(idx + 1) : basePackage;
    }

    private static void validatePackage(String pkg, String field) {
        if (StringUtils.isEmpty(pkg)) {
            return;
        }
        if (!PACKAGE_PATTERN.matcher(pkg).matches()) {
            throw new BizException("非法 Java 包名：" + field);
        }
    }

    private static void validateZipPath(String path, String field) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        if (path.contains("..") || path.startsWith("/") || path.contains(":")) {
            throw new BizException("非法路径（禁止 ..、绝对盘符）：" + field);
        }
        if (!SAFE_PATH_PATTERN.matcher(path).matches()) {
            throw new BizException("路径包含非法字符：" + field);
        }
    }
}
