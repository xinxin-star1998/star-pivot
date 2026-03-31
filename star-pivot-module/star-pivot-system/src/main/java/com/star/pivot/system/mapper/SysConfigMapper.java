package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.domain.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 参数配置Mapper接口
 *
 * @author admin
 * @date 2026-03-31
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
    /**
     * 分页查询参数配置列表
     *
     * @param page     分页参数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<SysConfig> selectPageList(Page<SysConfig> page, @Param("queryDTO") SysConfigQueryDTO queryDTO);

    /**
     * 查询参数配置列表（导出）
     *
     * @param queryDTO 查询条件
     * @return 参数配置列表
     */
    java.util.List<SysConfig> selectListByQuery(@Param("queryDTO") SysConfigQueryDTO queryDTO);

    /**
     * 根据主键查询参数配置
     *
     * @param configId 参数配置主键
     * @return 参数配置
     */
    SysConfig selectSysConfigByConfigId(@Param("configId") Long configId);

    /**
     * 新增参数配置
     *
     * @param sysConfig 参数配置
     * @return 影响行数
     */
    int insertSysConfig(SysConfig sysConfig);

    /**
     * 修改参数配置
     *
     * @param sysConfig 参数配置
     * @return 影响行数
     */
    int updateSysConfig(SysConfig sysConfig);

    /**
     * 批量删除参数配置
     *
     * @param configIds 参数配置主键数组
     * @return 影响行数
     */
    int deleteSysConfigByConfigIds(@Param("configIds") Long[] configIds);
}
