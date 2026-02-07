package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.system.domain.bo.OperLogReqBo;
import com.star.pivot.system.domain.bo.OperLogVO;
import com.star.pivot.system.domain.entity.SysOperLog;

/**
 * 操作日志服务接口
 *
 * @author xinxin
 * @since 2026-01-23
 */
public interface SysOperLogService extends IService<SysOperLog> {

    /**
     * 保存操作日志
     *
     * @param operLog 操作日志
     */
    void saveOperLog(SysOperLog operLog);

    /**
     * 分页查询操作日志
     *
     * @param operLogReqBo 查询参数
     * @return 分页结果
     */
    PageResponse<OperLogVO> pageList(OperLogReqBo operLogReqBo);
}
