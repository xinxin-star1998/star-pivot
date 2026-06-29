package com.star.pivot.framework.cache;

/**
 * 缓存名称常量
 * <p>
 * 统一管理项目中所有缓存名称，避免硬编码和命名冲突
 *
 * <p>使用示例：
 * <pre>
 * @Cacheable(cacheNames = CacheNames.USER_PERMISSIONS, key = "#username")
 * public Set<String> getUserPermissions(String username) { ... }
 * </pre>
 *
 * @author xinxin
 * @since 2026-01-25
 */
public final class CacheNames {

    private CacheNames() {
        // 禁止实例化
    }

    /**
     * 用户权限缓存
     * <p>key: username
     * <p>value: Set&lt;String&gt; 权限标识集合
     * <p>过期时间: 30分钟
     */
    public static final String USER_PERMISSIONS = "userPermissions";

    /**
     * 用户角色缓存
     * <p>key: userId
     * <p>value: List&lt;SysRole&gt; 角色列表
     * <p>过期时间: 30分钟
     */
    public static final String USER_ROLES = "userRoles";

    /**
     * 菜单树缓存
     * <p>key: "all" 或 userId
     * <p>value: List&lt;SysMenu&gt; 菜单树
     * <p>过期时间: 1小时
     */
    public static final String MENU_TREE = "menuTree";

    /**
     * 字典数据缓存
     * <p>key: dictType
     * <p>value: List&lt;DictData&gt; 字典数据列表
     * <p>过期时间: 1小时
     */
    public static final String DICT_DATA = "dictData";

    /**
     * 字典类型缓存
     * <p>key: dictType
     * <p>value: DictType 字典类型
     * <p>过期时间: 1小时
     */
    public static final String DICT_TYPE = "dictType";

    /**
     * 部门树缓存
     * <p>key: "all"
     * <p>value: List&lt;SysDept&gt; 部门树
     * <p>过期时间: 1小时
     */
    public static final String DEPT_TREE = "deptTree";

    /**
     * 岗位列表缓存
     * <p>key: "all"
     * <p>value: List&lt;SysPost&gt; 岗位列表
     * <p>过期时间: 1小时
     */
    public static final String POST_LIST = "postList";

    /**
     * 角色列表缓存
     * <p>key: "all"
     * <p>value: List&lt;SysRole&gt; 角色列表
     * <p>过期时间: 1小时
     */
    public static final String ROLE_LIST = "roleList";

    /**
     * 配置参数缓存
     * <p>key: configKey
     * <p>value: String 配置值
     * <p>过期时间: 2小时
     */
    public static final String SYS_CONFIG = "sysConfig";

    /**
     * 在线用户缓存
     * <p>key: token
     * <p>value: LoginUser 登录用户信息
     * <p>过期时间: 与token过期时间一致
     */
    public static final String ONLINE_USER = "onlineUser";

    /**
     * 验证码缓存
     * <p>key: uuid
     * <p>value: String 验证码
     * <p>过期时间: 5分钟
     */
    public static final String CAPTCHA = "captcha";

    /**
     * 登录失败次数缓存
     * <p>key: username 或 ip
     * <p>value: Integer 失败次数
     * <p>过期时间: 10分钟
     */
    public static final String LOGIN_FAIL_COUNT = "loginFailCount";

    /**
     * API限流缓存
     * <p>key: apiPath + ip/user
     * <p>value: Long 请求次数
     * <p>过期时间: 1分钟
     */
    public static final String RATE_LIMIT = "rateLimit";
}
