package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.api.OperLogApi;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.mapper.SysOperLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperLogApiImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements OperLogApi {

    @Override
    public void cleanAll() {
        log.info("开始清空所有操作日志");
        boolean removed = this.remove(null);
        log.info("清空操作日志完成, removed={}", removed);
    }

    @Override
    public void cleanBeforeDays(int days) {
        log.info("开始清理 {} 天前的操作日志", days);
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysOperLog::getOperTime, threshold);
        long count = this.count(wrapper);
        boolean removed = this.remove(wrapper);
        log.info("清理 {} 天前的操作日志完成, 删除记录数={}, removed={}", days, count, removed);
    }
}
