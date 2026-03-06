package com.star.pivot.monitor.config.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MonitorProperties 测试类
 *
 * @author xinxin
 * @since 2026-03-04
 */
class MonitorPropertiesTest {

    private MonitorProperties properties;

    @BeforeEach
    void setUp() {
        properties = new MonitorProperties();
    }

    @Test
    void testDefaultValues() {
        // Given & When
        properties = new MonitorProperties();

        // Then
        assertTrue(properties.getApiPerformanceEnabled());
        assertEquals(0.1, properties.getSampleRate());
        assertEquals(1000L, properties.getSlowApiThresholdMs());
        assertEquals(1000, properties.getQueueSize());
        assertEquals(100, properties.getBatchSize());
        assertEquals(5, properties.getBatchInterval());
    }

    @Test
    void testSetAndGetValues() {
        // Given
        properties.setApiPerformanceEnabled(false);
        properties.setSampleRate(0.2);
        properties.setSlowApiThresholdMs(2000L);
        properties.setQueueSize(500);
        properties.setBatchSize(50);
        properties.setBatchInterval(10);

        // When & Then
        assertFalse(properties.getApiPerformanceEnabled());
        assertEquals(0.2, properties.getSampleRate());
        assertEquals(2000L, properties.getSlowApiThresholdMs());
        assertEquals(500, properties.getQueueSize());
        assertEquals(50, properties.getBatchSize());
        assertEquals(10, properties.getBatchInterval());
    }
}
