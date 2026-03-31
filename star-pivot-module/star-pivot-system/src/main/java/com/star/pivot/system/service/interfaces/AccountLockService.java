package com.star.pivot.system.service.interfaces;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.utils.RedisCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 账户锁定服务
 * 实现登录失败次数统计和账户锁定功能
 * 
 * @author xinxin
 */
@Slf4j
@Service
public class AccountLockService {

    private final RedisCache redisCache;
    private final SysUserService sysUserService;

    public AccountLockService(RedisCache redisCache, @Lazy SysUserService sysUserService) {
        this.redisCache = redisCache;
        this.sysUserService = sysUserService;
    }

    /**
     * 账户锁定是否启用
     */
    @Value("${login.account-lock.enabled:true}")
    private boolean accountLockEnabled;

    /**
     * 连续失败N次后锁定账户
     */
    @Value("${login.account-lock.max-failures:5}")
    private int maxFailures;

    /**
     * 账户锁定时长（分钟）
     */
    @Value("${login.account-lock.lock-duration-minutes:5}")
    private int lockDurationMinutes;

    /**
     * 失败次数记录的过期时间（分钟），应大于锁定时长
     */
    @Value("${login.account-lock.failure-record-expire-minutes:10}")
    private int failureRecordExpireMinutes;

    /**
     * 检查账户是否被锁定
     * 
     * @param username 用户名
     * @throws ServiceException 如果账户被锁定
     */
    public void checkAccountLocked(String username) {
        if (!accountLockEnabled) {
            return;
        }

        String lockKey = getLockKey(username);
        if (redisCache.hasKey(lockKey)) {
            AccountLockInfo lockInfo = redisCache.getCacheObject(lockKey);
            long remainingSeconds = redisCache.getExpire(lockKey);
            long remainingMinutes = remainingSeconds > 0 ? (remainingSeconds + 59) / 60 : 0;
            
            if (lockInfo != null) {
                log.warn("账户 {} 已被锁定，锁定开始时间: {}, 剩余时间: {} 分钟", 
                    username, lockInfo.getLockStartTime(), remainingMinutes);
            } else {
                log.warn("账户 {} 已被锁定，剩余时间: {} 分钟", username, remainingMinutes);
            }
            
            throw new BizException(
                    ErrorCode.ACCOUNT_LOCKED,
                    String.format("账户已被锁定，请%d分钟后重试，或联系管理员解锁", remainingMinutes)
                        );
        }
    }

    /**
     * 记录登录失败
     * 如果失败次数达到阈值，则锁定账户
     * 
     * @param username 用户名
     */
    public void recordLoginFailure(String username) {
        if (!accountLockEnabled) {
            return;
        }

        String failureKey = getFailureCountKey(username);
        String lockKey = getLockKey(username);

        // 检查是否已锁定
        if (redisCache.hasKey(lockKey)) {
            log.debug("账户 {} 已锁定，跳过失败次数记录", username);
            return;
        }

        // 获取当前失败次数
        Integer currentFailures = redisCache.getCacheObject(failureKey);
        if (currentFailures == null) {
            currentFailures = 0;
        }

        // 增加失败次数
        int newFailures = currentFailures + 1;
        redisCache.setCacheObject(failureKey, newFailures, 
            failureRecordExpireMinutes, TimeUnit.MINUTES);

        log.info("账户 {} 登录失败，当前失败次数: {}/{}", username, newFailures, maxFailures);

        // 如果达到最大失败次数，锁定账户
        if (newFailures >= maxFailures) {
            lockAccount(username);
        }
    }

    /**
     * 清除登录失败记录（登录成功时调用）
     * 
     * @param username 用户名
     */
    public void clearLoginFailures(String username) {
        if (!accountLockEnabled) {
            return;
        }

        String failureKey = getFailureCountKey(username);
        redisCache.deleteObject(failureKey);
        log.debug("已清除账户 {} 的登录失败记录", username);
    }

    /**
     * 按用户ID解锁账户（管理员操作）
     * 内部完成：按 userId 查用户 → 判空 → 判用户名 → 判是否锁定 → 解锁。
     *
     * @param userId 用户ID
     * @return 明确结果（success + message），Controller 据此返回 Result.success/error
     */
    public UnlockResult unlockUserByUserId(Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return new UnlockResult(false, "用户不存在");
        }
        String username = user.getUserName();
        if (username == null || username.trim().isEmpty()) {
            return new UnlockResult(false, "用户名不能为空");
        }
        if (!isAccountLocked(username)) {
            return new UnlockResult(true, "账户未被锁定，无需解锁");
        }
        unlockAccount(username);
        return new UnlockResult(true, "账户已成功解锁");
    }

    /**
     * 解锁账户（管理员操作）
     * 
     * @param username 用户名
     */
    public void unlockAccount(String username) {
        String lockKey = getLockKey(username);
        String failureKey = getFailureCountKey(username);
        
        redisCache.deleteObject(lockKey);
        redisCache.deleteObject(failureKey);
        
        log.info("账户 {} 已被管理员解锁", username);
    }

    /**
     * 检查账户是否被锁定（不抛异常，仅返回状态）
     * 
     * @param username 用户名
     * @return true表示已锁定，false表示未锁定
     */
    public boolean isAccountLocked(String username) {
        if (!accountLockEnabled) {
            return false;
        }
        return redisCache.hasKey(getLockKey(username));
    }

    /**
     * 获取账户剩余锁定时间（秒）
     * 
     * @param username 用户名
     * @return 剩余锁定时间（秒），-1表示未锁定
     */
    public long getRemainingLockTime(String username) {
        String lockKey = getLockKey(username);
        if (!redisCache.hasKey(lockKey)) {
            return -1;
        }
        return redisCache.getExpire(lockKey);
    }

    /**
     * 获取账户锁定信息
     * 
     * @param username 用户名
     * @return 锁定信息，如果未锁定返回null
     */
    public AccountLockInfo getLockInfo(String username) {
        if (!accountLockEnabled) {
            return null;
        }
        String lockKey = getLockKey(username);
        if (!redisCache.hasKey(lockKey)) {
            return null;
        }
        return redisCache.getCacheObject(lockKey);
    }

    /**
     * 获取账户当前失败次数
     * 
     * @param username 用户名
     * @return 失败次数
     */
    public int getFailureCount(String username) {
        String failureKey = getFailureCountKey(username);
        Integer count = redisCache.getCacheObject(failureKey);
        return count != null ? count : 0;
    }

    /**
     * 锁定账户
     * 
     * @param username 用户名
     */
    private void lockAccount(String username) {
        String lockKey = getLockKey(username);
        
        // 创建锁定信息对象，包含锁定时间
        long lockStartTime = System.currentTimeMillis();
        long lockEndTime = lockStartTime + (lockDurationMinutes * 60 * 1000L);
        
        AccountLockInfo lockInfo = new AccountLockInfo();
        lockInfo.setLockStartTime(lockStartTime);
        lockInfo.setLockEndTime(lockEndTime);
        lockInfo.setLockDurationMinutes(lockDurationMinutes);
        lockInfo.setUsername(username);
        
        // 锁定账户，存储锁定信息对象，设置过期时间
        redisCache.setCacheObject(lockKey, lockInfo, 
            lockDurationMinutes, TimeUnit.MINUTES);
        
        log.warn("账户 {} 因连续{}次登录失败已被锁定，锁定开始时间: {}, 锁定时长: {} 分钟", 
            username, maxFailures, lockStartTime, lockDurationMinutes);
    }

    /**
     * 获取锁定键
     * 
     * @param username 用户名
     * @return Redis键
     */
    private String getLockKey(String username) {
        return "login:account-lock:" + username;
    }

    /**
     * 获取失败次数键
     * 
     * @param username 用户名
     * @return Redis键
     */
    private String getFailureCountKey(String username) {
        return "login:failure-count:" + username;
    }

    /**
     * 按 userId 解锁的结果，供 Controller 映射为 Result
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnlockResult implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private boolean success;
        private String message;
    }

    /**
     * 账户锁定信息
     * 存储在Redis中，包含锁定时间等详细信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountLockInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 用户名
         */
        private String username;

        /**
         * 锁定开始时间（时间戳，毫秒）
         */
        private Long lockStartTime;

        /**
         * 锁定结束时间（时间戳，毫秒）
         */
        private Long lockEndTime;

        /**
         * 锁定时长（分钟）
         */
        private Integer lockDurationMinutes;
    }
}
