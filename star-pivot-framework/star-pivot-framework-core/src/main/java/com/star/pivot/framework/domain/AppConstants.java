package com.star.pivot.framework.domain;

/**
 * 应用层通用常量（业务码、数据权限、菜单类型、状态等）
 */
public class AppConstants {

    public static final Integer SUCCESS = 200;
    public static final Integer FAIL = 500;
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String LOGOUT_SUCCESS = "退出成功";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String DELETE_SUCCESS = "删除成功";
    public static final String OPERATION_SUCCESS = "操作成功";
    public static final String OPERATION_FAIL = "操作失败";

    public static class DataScope {
        public static final String ALL = "1";
        public static final String CUSTOM = "2";
        public static final String DEPT = "3";
        public static final String DEPT_AND_CHILD = "4";
        public static final String SELF = "5";
    }

    public static class MenuType {
        public static final String CATALOG = "M";
        public static final String MENU = "C";
        public static final String BUTTON = "F";
    }

    public static class Status {
        public static final String NORMAL = "0";
        public static final String DISABLE = "1";
    }

    public static class Visible {
        public static final String SHOW = "0";
        public static final String HIDE = "1";
    }

    public static class YesNo {
        public static final String YES = "Y";
        public static final String NO = "N";
    }

    public static class DelFlag {
        public static final String NORMAL = "0";
        public static final String DELETE = "2";
    }

    public static class BusinessType {
        public static final int OTHER = 0;
        public static final int INSERT = 1;
        public static final int UPDATE = 2;
        public static final int DELETE = 3;
        public static final int GRANT = 4;
        public static final int EXPORT = 5;
        public static final int IMPORT = 6;
        public static final int FORCE = 7;
        public static final int GENCODE = 8;
        public static final int CLEAN = 9;
    }

    public static class OperStatus {
        public static final int NORMAL = 0;
        public static final int EXCEPTION = 1;
    }

    public static class LoginStatus {
        public static final String SUCCESS = "0";
        public static final String FAIL = "1";
    }

    public static class UserType {
        public static final int BACKEND = 1;
        public static final int MOBILE = 2;
    }

    public static final String ADMIN_ROLE_KEY = "admin";
    /** 注册用户默认角色 ID（新用户注册时分配，需与 sys_role 中普通角色一致） */
    public static final Long DEFAULT_REGISTER_ROLE_ID = 5L;
    public static final Long ADMIN_USER_ID = 1L;
    public static final String DEFAULT_PASSWORD = "123456";
}
