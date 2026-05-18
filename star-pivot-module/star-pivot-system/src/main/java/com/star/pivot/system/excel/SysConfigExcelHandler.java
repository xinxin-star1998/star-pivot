package com.star.pivot.system.excel;

import com.star.pivot.framework.excel.ExcelBizHandler;
import com.star.pivot.framework.excel.ExcelImportOptions;
import com.star.pivot.framework.excel.ExcelImportResult;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.bo.SysConfigVO;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.domain.excel.SysConfigExcel;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数配置 Excel 导出处理器（当前仅支持导出）
 */
@Component
@RequiredArgsConstructor
public class SysConfigExcelHandler implements ExcelBizHandler<SysConfigExcel, SysConfigQueryDTO> {

    private final ISysConfigService sysConfigService;

    @Override
    public List<SysConfigExcel> listForExport(SysConfigQueryDTO query) {
        SysConfigQueryDTO exportQuery = query != null ? query : new SysConfigQueryDTO();
        List<SysConfigVO> list = sysConfigService.selectSysConfigList(exportQuery);
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        List<SysConfigExcel> rows = new ArrayList<>(list.size());
        for (SysConfigVO item : list) {
            SysConfigExcel row = new SysConfigExcel();
            row.setConfigName(item.getConfigName());
            row.setConfigKey(item.getConfigKey());
            row.setConfigValue(item.getConfigValue());
            row.setConfigType(item.getConfigType());
            row.setRemark(item.getRemark());
            rows.add(row);
        }
        return rows;
    }

    @Override
    public ExcelImportResult importRows(List<SysConfigExcel> rows, ExcelImportOptions options) {
        throw new BizException("参数配置暂不支持 Excel 导入");
    }

    @Override
    public String sheetName(SysConfigQueryDTO query) {
        return "参数配置";
    }

    @Override
    public String exportFileName(SysConfigQueryDTO query) {
        return "sys_config_export_" + System.currentTimeMillis() + ".xlsx";
    }
}
