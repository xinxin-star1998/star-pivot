package com.star.pivot.system.domain.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import lombok.Data;

/**
 * 路由配置信息
 * 
 * @author StarPivot
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class RouterVo
{
    /**
     * 路由名字
     */
    private String name;

    /**
     * 菜单ID（供前端权限与菜单树衍生数据使用）
     */
    private Long menuId;

    /**
     * 权限标识（与菜单表一致，逗号分隔）
     */
    private String perms;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 是否为外链（0是 iframe/外链 1否），与 sys_menu.is_frame 一致
     */
    private Integer isFrame;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    private MetaVo meta;

    /**
     * 子路由
     */
    private List<RouterVo> children;
}
