package com.star.pivot.system.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.LogininforReqBo;
import com.star.pivot.system.domain.bo.LogininforVO;
import com.star.pivot.system.domain.entity.SysLogininfor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 按月份统计登录次数
     * @param start 开始时间
     * @param end 结束时间
     * @return List<Object[]> 每个元素为 [yearMonth, count]
     */
    List<Map<String, Object>> countByMonthRange(LocalDateTime start, LocalDateTime end);

    /**
     * 批量统计用户登录次数
     * @param userNames 用户名列表
     * @param startTime 开始时间
     * @return List<Object[]> 每个元素为 [userName, count]
     */
    List<Map<String, Object>> countByUserNames(List<String> userNames, LocalDateTime startTime);

}
