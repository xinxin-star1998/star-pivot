package com.star.pivot.system.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.SysConfigVO;
import com.star.pivot.system.domain.dto.SysConfigDTO;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.domain.entity.SysConfig;

/**
 * 参数配置Service接口
 *
 * @author admin
 * @since 2026-03-31
 */
public interface ISysConfigService extends IService<SysConfig> {
    /**
     * 分页查询参数配置列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResponse<SysConfigVO> selectSysConfigPage(SysConfigQueryDTO queryDTO);

    /**
     * 查询参数配置列表（导出）
     *
     * @param queryDTO 查询条件
     * @return 参数配置列表
     */
    java.util.List<SysConfigVO> selectSysConfigList(SysConfigQueryDTO queryDTO);

    /**
     * 根据主键查询参数配置详细信息
     *
     * @param configId 参数配置主键
     * @return 参数配置信息
     */
    SysConfigVO selectSysConfigByConfigId(Long configId);

    /**
     * 新增参数配置
     *
     * @param sysConfigDTO 参数配置信息
     * @return 是否成功
     */
    boolean insertSysConfig(SysConfigDTO sysConfigDTO);

    /**
     * 修改参数配置
     *
     * @param sysConfigDTO 参数配置信息
     * @return 是否成功
     */
    boolean updateSysConfig(SysConfigDTO sysConfigDTO);

    /**
     * 批量删除参数配置
     *
     * @param configIds 需要删除的参数配置主键数组
     * @return 是否成功
     */
    boolean deleteSysConfigByConfigIds(Long[] configIds);
}
