package com.star.pivot.generator.controller;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.common.sql.SqlUtil;
import com.star.pivot.generator.config.GenConfig;
import com.star.pivot.generator.domain.bo.GenTableVO;
import com.star.pivot.generator.domain.dto.GenTableQueryDTO;
import com.star.pivot.generator.domain.entity.GenTable;
import com.star.pivot.generator.domain.entity.GenTableColumn;
import com.star.pivot.generator.service.GenTableColumnService;
import com.star.pivot.generator.service.GenTableService;
import com.star.pivot.generator.utils.Convert;
import com.star.pivot.security.utils.SecurityContextUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成表管理控制器
 *
 * @author xinxin
 * @since 2025-01-17
 */
@Slf4j
@RestController
@RequestMapping("/tool/gen")
@RequiredArgsConstructor
public class GenController {
    
    private final GenTableService genTableService;
    private final GenTableColumnService genTableColumnService;

    /**
     * 分页查询代码生成表列表接口
     * 
     * @param queryDTO 代码生成表查询参数对象
     * @return 分页的代码生成表列表结果
     */
    @PreAuthorize("hasAuthority('tool:gen:query')")
    @PostMapping("/list")
    public Result<PageResponse<GenTableVO>> list(@RequestBody GenTableQueryDTO queryDTO) {
        PageResponse<GenTableVO> page = genTableService.selectGenTablePage(queryDTO);
        return Result.success(page);
    }
    /**
     * 查询数据库列表
     */
    @PostMapping("/db/list")
    public Result<PageResponse<GenTableVO>> dbTableList(@RequestBody GenTableQueryDTO queryDTO)
    {
        PageResponse<GenTableVO> pageResponse = genTableService.selectDbTableList(queryDTO);
        return Result.success(pageResponse);
    }
    /**
     * 查询数据表字段列表
     */
    @GetMapping(value = "/column/{tableId}")
    public Result<List<GenTableColumn>> columnList(@PathVariable Long tableId)
    {
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        return Result.success(list);
    }

    /**
     * 导入表结构（保存）
     */
    @PostMapping("/importTable")
    public Result<?> importTableSave(@RequestBody Map<String, String> body)
    {
        String tables = body.get("tables");
        if (tables == null || tables.isBlank()) {
            return Result.error("表名不能为空");
        }
        String[] tableNames = Convert.toStrArray(tables);
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        //获取当前登录用户名
        String operName = SecurityContextUtils.getUsername();
        genTableService.importGenTable(tableList, operName);
        return Result.success();
    }
    /**
     * 获取代码生成信息
     */
    @GetMapping(value = "/{tableId}")
    public Result<Map<String,Object>> getInfo(@PathVariable Long tableId)
    {
        GenTable table = genTableService.selectGenTableById(tableId);
        if (table == null)
        {
            return Result.error("代码生成表不存在，表ID：" + tableId);
        }
        List<GenTable> tables = genTableService.selectGenTableAll();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("info", table);
        map.put("rows", list);
        map.put("tables", tables);
        return Result.success(map);
    }

     /**
     * 创建表结构（保存）
     * 
     * <p>注意：此接口需要 admin 角色或 tool:gen:add 权限
     */
    @PreAuthorize("@ss.hasRole('admin') or hasAuthority('tool:gen:add')")
    @PostMapping("/createTable")
    public Result<?> createTableSave(@RequestBody Map<String, String> body) {
        try {
            String sql = body.get("tableSql");
            if (sql == null || sql.isBlank()) {
                return Result.error("SQL不能为空");
            }
            SqlUtil.filterKeyword(sql);
            List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
            List<String> tableNames = new ArrayList<>();
            for (SQLStatement sqlStatement : sqlStatements) {
                if (sqlStatement instanceof MySqlCreateTableStatement) {
                    MySqlCreateTableStatement createTableStatement = (MySqlCreateTableStatement) sqlStatement;
                    // 提取表名（去掉反引号）
                    String tableName = createTableStatement.getTableName().replaceAll("`", "");
                    // 如果表已经存在，则跳过建表，仅做导入
                    List<GenTable> existed = genTableService.selectDbTableListByNames(new String[]{tableName});
                    if (existed != null && !existed.isEmpty()) {
                        tableNames.add(tableName);
                        continue;
                    }
                    // 表不存在时才真正执行建表
                    if (genTableService.createTable(createTableStatement.toString())) {
                        tableNames.add(tableName);
                    }
                }
            }
            List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames.toArray(new String[0]));
            // 获取当前用户
            String operName = SecurityContextUtils.getUsername();
            genTableService.importGenTable(tableList, operName);
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("创建表结构异常");
        }
    }
    /**
     * 修改保存代码生成业务
     */
    @PreAuthorize("hasAuthority('tool:gen:edit')")
    @PostMapping("/editSave")
    public Result<?> editSave(@Validated @RequestBody GenTable genTable)
    {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return Result.success("修改成功");
    }
    /**
     * 删除代码生成
     */
    @PreAuthorize("hasAuthority('tool:gen:delete')")
    @DeleteMapping("/{tableIds}")
    public Result<?> remove(@PathVariable Long[] tableIds)
    {
        genTableService.deleteGenTableByIds(tableIds);
        return Result.success();
    }
    /**
     * 预览代码
     */
    @PreAuthorize("hasAuthority('tool:gen:preview')")
    @GetMapping("/preview/{tableId}")
    public Result<Map<String, String>> preview(@PathVariable("tableId") Long tableId) throws IOException
    {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return Result.success(dataMap);
    }
    /**
     * 生成代码（下载方式）
     */
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @PreAuthorize("hasAuthority('tool:gen:create')")
    @GetMapping("/genCode/{tableName}")
    public Result<?> genCode(@PathVariable("tableName") String tableName)
    {
        if (!GenConfig.isAllowOverwrite())
        {
            return Result.error("【系统预设】不允许生成文件覆盖到本地");
        }
        genTableService.generatorCode(tableName);
        return Result.success();
    }

    /**
     * 同步数据库
     */
    @PreAuthorize("hasAuthority('tool:gen:sync')")
    @GetMapping("/syncDb/{tableName}")
    public Result<?> syncDb(@PathVariable("tableName") String tableName)
    {
        genTableService.synchDb(tableName);
        return Result.success();
    }

    /**
     * 批量生成代码
     */
    @PreAuthorize("hasAuthority('tool:gen:create')")
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException
    {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"starPivot.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}
