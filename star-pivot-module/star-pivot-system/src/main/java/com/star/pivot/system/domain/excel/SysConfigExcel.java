package com.star.pivot.system.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 参数配置 Excel 行（EasyExcel 导出）
 */
@Data
public class SysConfigExcel {

    @ExcelProperty("参数名称")
    private String configName;

    @ExcelProperty("参数键名")
    private String configKey;

    @ExcelProperty("参数键值")
    private String configValue;

    @ExcelProperty("系统内置")
    private String configType;

    @ExcelProperty("备注")
    private String remark;
}
