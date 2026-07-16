package com.star.pivot.system.constants;

/**
 * sys_config 参数键名常量
 */
public final class SysConfigKeys {

    private SysConfigKeys() {
    }

    /** 用户管理 - 账号初始密码 */
    public static final String USER_INIT_PASSWORD = "sys.user.initPassword";

    /** 账号自助 - 验证码开关（true/false） */
    public static final String ACCOUNT_CAPTCHA_ENABLED = "sys.account.captchaEnabled";

    /** 账号自助 - 是否开启用户自助注册（true/false） */
    public static final String REGISTER_USER = "sys.account.registerUser";

    /** 用户登录 - IP 黑名单（多个以 ; 分隔，支持 * 通配与网段前缀） */
    public static final String LOGIN_BLACK_IP_LIST = "sys.login.blackIPList";

    /** 用户管理 - 初始密码修改策略（0 关闭，1 登录提醒修改） */
    public static final String ACCOUNT_INIT_PASSWORD_MODIFY = "sys.account.initPasswordModify";

    /** 用户管理 - 账号密码更新周期（天，0 表示不限制） */
    public static final String ACCOUNT_PASSWORD_VALIDATE_DAYS = "sys.account.passwordValidateDays";

    /** 账号自助 - 验证码位数 */
    public static final String ACCOUNT_CAPTCHA_LENGTH = "sys.account.captchaLength";

    /** 账号自助 - 验证码过期时间（秒） */
    public static final String ACCOUNT_CAPTCHA_EXPIRE_SECONDS = "sys.account.captchaExpireSeconds";

    /** 账号自助 - 验证码类型（image / slider / click） */
    public static final String ACCOUNT_CAPTCHA_TYPE = "sys.account.captchaType";

    /** 默认值：初始密码 */
    public static final String DEFAULT_INIT_PASSWORD = "123456";

    /** 默认值：验证码开启 */
    public static final boolean DEFAULT_CAPTCHA_ENABLED = true;

    /** 默认值：验证码类型 */
    public static final String DEFAULT_CAPTCHA_TYPE = "image";

    /** 默认值：初始密码修改策略关闭 */
    public static final int DEFAULT_INIT_PASSWORD_MODIFY = 0;

    /** 默认值：密码周期不限制 */
    public static final int DEFAULT_PASSWORD_VALIDATE_DAYS = 0;

    /** 默认值：验证码位数 */
    public static final int DEFAULT_CAPTCHA_LENGTH = 4;

    /** 默认值：验证码过期时间（秒） */
    public static final int DEFAULT_CAPTCHA_EXPIRE_SECONDS = 180;

    /** 验证码位数下限 */
    public static final int MIN_CAPTCHA_LENGTH = 4;

    /** 验证码位数上限 */
    public static final int MAX_CAPTCHA_LENGTH = 6;

    /** 验证码过期时间下限（秒） */
    public static final int MIN_CAPTCHA_EXPIRE_SECONDS = 60;

    /** 验证码过期时间上限（秒） */
    public static final int MAX_CAPTCHA_EXPIRE_SECONDS = 600;
}
