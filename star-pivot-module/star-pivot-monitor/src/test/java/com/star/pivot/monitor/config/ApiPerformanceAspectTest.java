package com.star.pivot.monitor.config;

import com.star.pivot.monitor.config.properties.MonitorProperties;
import com.star.pivot.monitor.mapper.SysMonitorApiPerformanceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * ApiPerformanceAspect 测试类
 *
 * @author xinxin
 * @since 2026-03-04
 */
class ApiPerformanceAspectTest {

    @Mock
    private SysMonitorApiPerformanceMapper mapper;

    @Mock
    private MonitorProperties monitorProperties;

    @InjectMocks
    private ApiPerformanceAspect apiPerformanceAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStats() {
        // Given
        when(monitorProperties.getSampleRate()).thenReturn(0.1);
        when(monitorProperties.getSlowApiThresholdMs()).thenReturn(1000L);

        // When
        var stats = apiPerformanceAspect.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(0L, stats.getTotalRequests());
        assertEquals(0L, stats.getSlowApiRequests());
        assertEquals(0.1, stats.getSampleRate());
    }

    @Test
    void testApiPerformanceStats() {
        // Given
        long totalRequests = 1000L;
        long slowApiRequests = 100L;
        long sampledRequests = 50L;
        long queueSize = 10L;
        long slowApiThreshold = 1000L;
        double sampleRate = 0.1;
        double slowApiRate = 10.0;

        // When
        ApiPerformanceAspect.ApiPerformanceStats stats = new ApiPerformanceAspect.ApiPerformanceStats(
                totalRequests, slowApiRequests, sampledRequests, queueSize,
                slowApiThreshold, sampleRate, slowApiRate
        );

        // Then
        assertEquals(totalRequests, stats.getTotalRequests());
        assertEquals(slowApiRequests, stats.getSlowApiRequests());
        assertEquals(sampledRequests, stats.getSampledRequests());
        assertEquals(queueSize, stats.getQueueSize());
        assertEquals(slowApiThreshold, stats.getSlowApiThreshold());
        assertEquals(sampleRate, stats.getSampleRate());
        assertEquals(slowApiRate, stats.getSlowApiRate());
    }
}
