package com.star.pivot.system.service.impl;

import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.system.domain.bo.ConsoleDashboardVO;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.domain.entity.SysNotice;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.interfaces.ConsoleDashboardService;
import com.star.pivot.system.service.interfaces.ISysNoticeService;
import com.star.pivot.system.service.interfaces.SysLogininforService;
import com.star.pivot.system.service.interfaces.SysOperLogService;
import com.star.pivot.system.service.interfaces.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作台首页聚合服务实现
 */
@Service
@RequiredArgsConstructor
public class ConsoleDashboardServiceImpl implements ConsoleDashboardService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M月" );
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm" );

    private final SysUserService sysUserService;
    private final SysLogininforService sysLogininforService;
    private final SysOperLogService sysOperLogService;
    private final ISysNoticeService sysNoticeService;

    @Override
    public ConsoleDashboardVO getConsoleData() {
        ConsoleDashboardVO vo = new ConsoleDashboardVO();
        vo.setCardList(buildCards());
        vo.setVisitTrend(buildVisitTrend());
        vo.setUserTrend(buildUserTrend());
        vo.setTodoList(buildTodoList());
        vo.setDynamicList(buildDynamicList());
        vo.setNewUserList(buildNewUserList());
        return vo;
    }

    private List<ConsoleDashboardVO.CardItem> buildCards() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();
        LocalDateTime weekStart = today.minusDays(6).atStartOfDay();
        LocalDateTime prevWeekStart = today.minusDays(13).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime prevMonthStart = monthStart.minusMonths(1);

        long totalVisit = sysLogininforService.count();
        long todayVisit = countLoginBetween(todayStart, LocalDateTime.now());
        long yesterdayVisit = countLoginBetween(yesterdayStart, todayStart);

        long onlineLikeCount = countLoginBetween(LocalDateTime.now().minusMinutes(30), LocalDateTime.now());
        long todayOnlineLike = countLoginBetween(todayStart, LocalDateTime.now());
        long yesterdayOnlineLike = countLoginBetween(yesterdayStart, todayStart);

        long totalOperate = sysOperLogService.count();
        long weekOperate = countOperBetween(weekStart, LocalDateTime.now());
        long prevWeekOperate = countOperBetween(prevWeekStart, weekStart);

        long monthNewUser = countUserBetween(monthStart, LocalDateTime.now());
        long prevMonthNewUser = countUserBetween(prevMonthStart, monthStart);

        List<ConsoleDashboardVO.CardItem> cards = new ArrayList<>(4);
        cards.add(buildCard("总访问次数" , "ri:pie-chart-line" , totalVisit, calcChange(todayVisit, yesterdayVisit)));
        cards.add(buildCard("在线访客数" , "ri:group-line" , onlineLikeCount, calcChange(todayOnlineLike, yesterdayOnlineLike)));
        cards.add(buildCard("点击量" , "ri:fire-line" , totalOperate, calcChange(weekOperate, prevWeekOperate)));
        cards.add(buildCard("新用户" , "ri:progress-2-line" , monthNewUser, calcChange(monthNewUser, prevMonthNewUser)));
        return cards;
    }

    private ConsoleDashboardVO.TrendData buildVisitTrend() {
        List<String> xAxis = new ArrayList<>(12);
        List<Long> data = new ArrayList<>(12);
        LocalDate firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        for (int i = 11; i >= 0; i--) {
            LocalDate month = firstDayOfCurrentMonth.minusMonths(i);
            LocalDateTime start = month.atStartOfDay();
            LocalDateTime end = month.plusMonths(1).atStartOfDay();
            xAxis.add(month.format(MONTH_FORMATTER));
            data.add(countLoginBetween(start, end));
        }
        ConsoleDashboardVO.TrendData trendData = new ConsoleDashboardVO.TrendData();
        trendData.setXAxisData(xAxis);
        trendData.setData(data);
        return trendData;
    }

    private ConsoleDashboardVO.TrendData buildUserTrend() {
        List<String> xAxis = new ArrayList<>(12);
        List<Long> data = new ArrayList<>(12);
        LocalDate firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        for (int i = 11; i >= 0; i--) {
            LocalDate month = firstDayOfCurrentMonth.minusMonths(i);
            LocalDateTime start = month.atStartOfDay();
            LocalDateTime end = month.plusMonths(1).atStartOfDay();
            xAxis.add(month.format(MONTH_FORMATTER));
            data.add(countUserBetween(start, end));
        }
        ConsoleDashboardVO.TrendData trendData = new ConsoleDashboardVO.TrendData();
        trendData.setXAxisData(xAxis);
        trendData.setData(data);
        return trendData;
    }

    private List<ConsoleDashboardVO.TodoItem> buildTodoList() {
        List<SysNotice> noticeList = sysNoticeService.lambdaQuery()
                .eq(SysNotice::getStatus, AppConstants.Status.NORMAL)
                .orderByDesc(SysNotice::getCreateTime)
                .last("limit 6" )
                .list();

        List<ConsoleDashboardVO.TodoItem> todoList = new ArrayList<>(noticeList.size());
        for (SysNotice notice : noticeList) {
            ConsoleDashboardVO.TodoItem item = new ConsoleDashboardVO.TodoItem();
            item.setUsername(notice.getNoticeTitle());
            item.setDate(formatDateTime(notice.getCreateTime()));
            item.setComplete(false);
            todoList.add(item);
        }
        return todoList;
    }

    private List<ConsoleDashboardVO.DynamicItem> buildDynamicList() {
        List<SysOperLog> operLogs = sysOperLogService.lambdaQuery()
                .orderByDesc(SysOperLog::getOperTime)
                .last("limit 8" )
                .list();

        List<ConsoleDashboardVO.DynamicItem> dynamicList = new ArrayList<>(operLogs.size());
        for (SysOperLog log : operLogs) {
            ConsoleDashboardVO.DynamicItem item = new ConsoleDashboardVO.DynamicItem();
            item.setUsername(StringUtils.hasText(log.getOperName()) ? log.getOperName() : "系统" );
            item.setType(resolveBusinessType(log.getBusinessType()));
            item.setTarget(StringUtils.hasText(log.getTitle()) ? log.getTitle() : safe(log.getOperUrl()));
            dynamicList.add(item);
        }
        return dynamicList;
    }

    private List<ConsoleDashboardVO.NewUserItem> buildNewUserList() {
        List<SysUser> users = sysUserService.lambdaQuery()
                .eq(SysUser::getDelFlag, AppConstants.DelFlag.NORMAL)
                .orderByDesc(SysUser::getCreateTime)
                .last("limit 6" )
                .list();

        List<ConsoleDashboardVO.NewUserItem> newUserList = new ArrayList<>(users.size());
        for (SysUser user : users) {
            ConsoleDashboardVO.NewUserItem item = new ConsoleDashboardVO.NewUserItem();
            item.setUsername(StringUtils.hasText(user.getNickName()) ? user.getNickName() : user.getUserName());
            item.setProvince("--" );
            item.setSex(parseSex(user.getSex()));
            item.setPercentage(calculateUserActivity(user.getUserName()));
            newUserList.add(item);
        }
        return newUserList;
    }

    private long countLoginBetween(LocalDateTime start, LocalDateTime end) {
        return sysLogininforService.lambdaQuery()
                .ge(SysLogininfor::getLoginTime, start)
                .lt(SysLogininfor::getLoginTime, end)
                .count();
    }

    private long countOperBetween(LocalDateTime start, LocalDateTime end) {
        return sysOperLogService.lambdaQuery()
                .ge(SysOperLog::getOperTime, start)
                .lt(SysOperLog::getOperTime, end)
                .count();
    }

    private long countUserBetween(LocalDateTime start, LocalDateTime end) {
        return sysUserService.lambdaQuery()
                .eq(SysUser::getDelFlag, AppConstants.DelFlag.NORMAL)
                .ge(SysUser::getCreateTime, start)
                .lt(SysUser::getCreateTime, end)
                .count();
    }

    private String calcChange(long current, long previous) {
        if (previous <= 0) {
            return current > 0 ? "+100%" : "+0%";
        }
        BigDecimal ratio = BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 0, RoundingMode.HALF_UP);
        String sign = ratio.signum() >= 0 ? "+" : "";
        return sign + ratio.toPlainString() + "%";
    }

    private ConsoleDashboardVO.CardItem buildCard(String des, String icon, Long num, String change) {
        ConsoleDashboardVO.CardItem cardItem = new ConsoleDashboardVO.CardItem();
        cardItem.setDes(des);
        cardItem.setIcon(icon);
        cardItem.setNum(num);
        cardItem.setChange(change);
        return cardItem;
    }

    private String resolveBusinessType(Integer businessType) {
        if (businessType == null) {
            return "执行了";
        }
        return switch (businessType) {
            case AppConstants.BusinessType.INSERT -> "新增了";
            case AppConstants.BusinessType.UPDATE -> "修改了";
            case AppConstants.BusinessType.DELETE -> "删除了";
            case AppConstants.BusinessType.EXPORT -> "导出了";
            case AppConstants.BusinessType.IMPORT -> "导入了";
            default -> "执行了";
        };
    }

    private Integer parseSex(String sex) {
        if ("0".equals(sex)) {
            return 1;
        }
        return 0;
    }

    private int calculateUserActivity(String userName) {
        if (!StringUtils.hasText(userName)) {
            return 0;
        }
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long loginCount = sysLogininforService.lambdaQuery()
                .eq(SysLogininfor::getUserName, userName)
                .ge(SysLogininfor::getLoginTime, monthStart)
                .count();
        return (int) Math.min(loginCount * 10, 100);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATE_TIME_FORMATTER);
    }

    private String safe(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }
}
