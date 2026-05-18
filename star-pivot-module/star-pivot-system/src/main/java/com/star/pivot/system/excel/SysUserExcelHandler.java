package com.star.pivot.system.excel;

import com.star.pivot.framework.excel.ExcelBizHandler;
import com.star.pivot.framework.excel.ExcelImportOptions;
import com.star.pivot.framework.excel.ExcelImportResult;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.excel.SysUserExcel;
import com.star.pivot.system.service.interfaces.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户 Excel 导入导出处理器
 */
@Component
@RequiredArgsConstructor
public class SysUserExcelHandler implements ExcelBizHandler<SysUserExcel, UserReqBo> {

    private final SysUserService sysUserService;

    @Override
    public List<SysUserExcel> listForExport(UserReqBo query) {
        return sysUserService.listForExport(query);
    }

    @Override
    public ExcelImportResult importRows(List<SysUserExcel> rows, ExcelImportOptions options) {
        return sysUserService.importFromExcel(rows, options.isUpdateSupport());
    }

    @Override
    public String sheetName(UserReqBo query) {
        return "用户";
    }

    @Override
    public String exportFileName(UserReqBo query) {
        return "user_export_" + System.currentTimeMillis() + ".xlsx";
    }

    @Override
    public String templateFileName(UserReqBo query) {
        return "user_import_template.xlsx";
    }

    @Override
    public List<SysUserExcel> templateSampleRows(UserReqBo query) {
        SysUserExcel sample = new SysUserExcel();
        sample.setUserName("test001");
        sample.setNickName("测试用户");
        sample.setEmail("test@example.com");
        sample.setPhonenumber("13800138000");
        sample.setSex("男");
        sample.setStatus("正常");
        sample.setDeptId(1L);
        sample.setRemark("示例数据");
        return List.of(sample);
    }
}
