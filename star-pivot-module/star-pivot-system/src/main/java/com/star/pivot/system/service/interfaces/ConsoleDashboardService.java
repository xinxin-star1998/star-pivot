package com.star.pivot.system.service.interfaces;

import com.star.pivot.system.domain.bo.ConsoleDashboardVO;

/**
 * 工作台首页聚合服务
 */
public interface ConsoleDashboardService {

    /**
     * 获取首页真实业务数据
     *
     * @return 首页数据
     */
    ConsoleDashboardVO getConsoleData();
}
