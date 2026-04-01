package com.star.pivot.system.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * 工作台首页数据
 */
@Data
public class ConsoleDashboardVO {

    /**
     * 顶部统计卡片
     */
    private List<CardItem> cardList;

    /**
     * 访问趋势（近12个月）
     */
    private TrendData visitTrend;

    /**
     * 用户趋势（近12个月新增用户）
     */
    private TrendData userTrend;

    /**
     * 待办事项（由公告/通知生成）
     */
    private List<TodoItem> todoList;

    /**
     * 最新动态（操作日志）
     */
    private List<DynamicItem> dynamicList;

    /**
     * 新用户列表
     */
    private List<NewUserItem> newUserList;

    @Data
    public static class CardItem {
        private String des;
        private String icon;
        private Long num;
        private String change;
    }

    @Data
    public static class TrendData {
        private List<String> xAxisData;
        private List<Long> data;
    }

    @Data
    public static class TodoItem {
        private String username;
        private String date;
        private Boolean complete;
    }

    @Data
    public static class DynamicItem {
        private String username;
        private String type;
        private String target;
    }

    @Data
    public static class NewUserItem {
        private String username;
        private String province;
        private Integer sex;
        private Integer percentage;
    }
}
