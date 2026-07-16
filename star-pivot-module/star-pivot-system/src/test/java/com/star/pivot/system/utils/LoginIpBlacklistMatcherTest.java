package com.star.pivot.system.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginIpBlacklistMatcherTest {

    @Test
    void shouldNotBlockWhenConfigEmpty() {
        assertFalse(LoginIpBlacklistMatcher.isBlocked("192.168.1.10", ""));
        assertFalse(LoginIpBlacklistMatcher.isBlocked("192.168.1.10", null));
    }

    @Test
    void shouldBlockExactIp() {
        assertTrue(LoginIpBlacklistMatcher.isBlocked("10.0.0.8", "10.0.0.8;192.168.1.1"));
        assertFalse(LoginIpBlacklistMatcher.isBlocked("10.0.0.9", "10.0.0.8"));
    }

    @Test
    void shouldBlockWildcardPattern() {
        assertTrue(LoginIpBlacklistMatcher.isBlocked("192.168.3.21", "192.168.*"));
        assertFalse(LoginIpBlacklistMatcher.isBlocked("10.0.0.1", "192.168.*"));
    }

    @Test
    void shouldBlockPrefixSegment() {
        assertTrue(LoginIpBlacklistMatcher.isBlocked("192.168.10.2", "192.168."));
        assertFalse(LoginIpBlacklistMatcher.isBlocked("10.192.168.2", "192.168."));
    }
}
