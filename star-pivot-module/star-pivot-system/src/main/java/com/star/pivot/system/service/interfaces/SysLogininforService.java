package com.star.pivot.system.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.LogininforReqBo;
import com.star.pivot.system.domain.bo.LogininforVO;
import com.star.pivot.system.domain.entity.SysLogininfor;

/**
 * 登录日志服务接口
 *
 * @author xinxin
 * @since 2026-01-23
 */
public interface SysLogininforService extends IService<SysLogininfor> {

    /**
     * 保存登录日志
     *
     * @param logininfor 登录日志
     */
    void saveLogininfor(SysLogininfor logininfor);

    /**
     * 分页查询登录日志
     *
     * @param logininforReqBo 查询参数
     * @return 分页结果
     */
    PageResponse<LogininforVO> pageList(LogininforReqBo logininforReqBo);
}
