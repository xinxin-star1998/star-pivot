package com.star.pivot.system.service.cache;

import com.star.pivot.framework.cache.CacheNames;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheNames 常量类测试
 *
 * @author xinxin
 * @since 2026-06-07
 */
@DisplayName("缓存名称常量测试")
class CacheNamesTest {

    @Test
    @DisplayName("测试缓存名称常量值正确性")
    void testCacheNameConstants() {
        // 验证所有缓存名称常量的值
        assertEquals("userPermissions", CacheNames.USER_PERMISSIONS);
        assertEquals("userRoles", CacheNames.USER_ROLES);
        assertEquals("menuTree", CacheNames.MENU_TREE);
        assertEquals("dictData", CacheNames.DICT_DATA);
        assertEquals("dictType", CacheNames.DICT_TYPE);
        assertEquals("deptTree", CacheNames.DEPT_TREE);
        assertEquals("postList", CacheNames.POST_LIST);
        assertEquals("roleList", CacheNames.ROLE_LIST);
        assertEquals("sysConfig", CacheNames.SYS_CONFIG);
        assertEquals("onlineUser", CacheNames.ONLINE_USER);
        assertEquals("captcha", CacheNames.CAPTCHA);
        assertEquals("loginFailCount", CacheNames.LOGIN_FAIL_COUNT);
        assertEquals("rateLimit", CacheNames.RATE_LIMIT);
    }

    @Test
    @DisplayName("测试缓存名称唯一性")
    void testCacheNameUniqueness() {
        // 确保所有缓存名称都是唯一的，避免冲突
        String[] cacheNames = {
                CacheNames.USER_PERMISSIONS,
                CacheNames.USER_ROLES,
                CacheNames.MENU_TREE,
                CacheNames.DICT_DATA,
                CacheNames.DICT_TYPE,
                CacheNames.DEPT_TREE,
                CacheNames.POST_LIST,
                CacheNames.ROLE_LIST,
                CacheNames.SYS_CONFIG,
                CacheNames.ONLINE_USER,
                CacheNames.CAPTCHA,
                CacheNames.LOGIN_FAIL_COUNT,
                CacheNames.RATE_LIMIT
        };

        // 检查是否有重复
        for (int i = 0; i < cacheNames.length; i++) {
            for (int j = i + 1; j < cacheNames.length; j++) {
                assertNotEquals(cacheNames[i], cacheNames[j],
                        "缓存名称重复: " + cacheNames[i]);
            }
        }
    }

    @Test
    @DisplayName("测试缓存名称非空")
    void testCacheNamesNotEmpty() {
        assertNotNull(CacheNames.USER_PERMISSIONS);
        assertFalse(CacheNames.USER_PERMISSIONS.isEmpty());

        assertNotNull(CacheNames.MENU_TREE);
        assertFalse(CacheNames.MENU_TREE.isEmpty());

        assertNotNull(CacheNames.DICT_DATA);
        assertFalse(CacheNames.DICT_DATA.isEmpty());
    }

    @Test
    @DisplayName("测试类不可实例化")
    void testClassCannotBeInstantiated() {
        // CacheNames 是工具类，不应该被实例化
        assertThrows(Exception.class, () -> {
            var constructor = CacheNames.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}
