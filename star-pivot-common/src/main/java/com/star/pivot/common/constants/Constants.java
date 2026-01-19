package com.star.pivot.common.constants;

/**
 * 通用常量
 *
 * @author stardust
 * @since 2024-01-01
 */
public class Constants {

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "登录成功";

    /**
     * 退出成功
     */
    public static final String LOGOUT_SUCCESS = "退出成功";

    /**
     * 注册成功
     */
    public static final String REGISTER_SUCCESS = "注册成功";

    /**
     * 删除成功
     */
    public static final String DELETE_SUCCESS = "删除成功";

    /**
     * 操作成功
     */
    public static final String OPERATION_SUCCESS = "操作成功";

    /**
     * 操作失败
     */
    public static final String OPERATION_FAIL = "操作失败";

    /**
     * 数据权限范围
     */
    public static class DataScope {
        /**
         * 全部数据权限
         */
        public static final String ALL = "1";

        /**
         * 自定数据权限
         */
        public static final String CUSTOM = "2";

        /**
         * 本部门数据权限
         */
        public static final String DEPT = "3";

        /**
         * 本部门及以下数据权限
         */
        public static final String DEPT_AND_CHILD = "4";

        /**
         * 仅本人数据权限
         */
        public static final String SELF = "5";
    }

    /**
     * 菜单类型
     */
    public static class MenuType {
        /**
         * 目录
         */
        public static final String CATALOG = "M";

        /**
         * 菜单
         */
        public static final String MENU = "C";

        /**
         * 按钮
         */
        public static final String BUTTON = "F";
    }

    /**
     * 状态
     */
    public static class Status {
        /**
         * 正常
         */
        public static final String NORMAL = "0";

        /**
         * 停用
         */
        public static final String DISABLE = "1";
    }

    /**
     * 显示状态
     */
    public static class Visible {
        /**
         * 显示
         */
        public static final String SHOW = "0";

        /**
         * 隐藏
         */
        public static final String HIDE = "1";
    }

    /**
     * 是否
     */
    public static class YesNo {
        /**
         * 是
         */
        public static final String YES = "Y";

        /**
         * 否
         */
        public static final String NO = "N";
    }

    /**
     * 删除标志
     */
    public static class DelFlag {
        /**
         * 正常
         */
        public static final String NORMAL = "0";

        /**
         * 删除
         */
        public static final String DELETE = "2";
    }

    /**
     * 操作类型
     */
    public static class BusinessType {
        /**
         * 其它
         */
        public static final int OTHER = 0;

        /**
         * 新增
         */
        public static final int INSERT = 1;

        /**
         * 修改
         */
        public static final int UPDATE = 2;

        /**
         * 删除
         */
        public static final int DELETE = 3;

        /**
         * 授权
         */
        public static final int GRANT = 4;

        /**
         * 导出
         */
        public static final int EXPORT = 5;

        /**
         * 导入
         */
        public static final int IMPORT = 6;

        /**
         * 强退
         */
        public static final int FORCE = 7;

        /**
         * 生成代码
         */
        public static final int GENCODE = 8;

        /**
         * 清空数据
         */
        public static final int CLEAN = 9;
    }

    /**
     * 操作状态
     */
    public static class OperStatus {
        /**
         * 正常
         */
        public static final int NORMAL = 0;

        /**
         * 异常
         */
        public static final int EXCEPTION = 1;
    }

    /**
     * 登录状态
     */
    public static class LoginStatus {
        /**
         * 成功
         */
        public static final String SUCCESS = "0";

        /**
         * 失败
         */
        public static final String FAIL = "1";
    }

    /**
     * 用户类型
     */
    public static class UserType {
        /**
         * 后台用户
         */
        public static final int BACKEND = 1;

        /**
         * 手机端用户
         */
        public static final int MOBILE = 2;
    }

    /**
     * 超级管理员角色标识
     */
    public static final String ADMIN_ROLE_KEY = "admin";

    /**
     * 超级管理员用户ID
     */
    public static final Long ADMIN_USER_ID = 1L;

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";
}

