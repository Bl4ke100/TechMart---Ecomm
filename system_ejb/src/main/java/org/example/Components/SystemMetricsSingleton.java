package org.example.Components;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.annotation.PostConstruct;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.service.SystemMetricsService;

@Singleton
@Startup
public class SystemMetricsSingleton implements SystemMetricsService {

    private AtomicInteger activeCartSessions = new AtomicInteger(0);

    private MemoryMXBean memoryBean;
    private ThreadMXBean threadBean;
    private RuntimeMXBean runtimeBean;

    @PostConstruct
    public void init() {
        memoryBean = ManagementFactory.getMemoryMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
    }

    public void incrementSession() {
        activeCartSessions.incrementAndGet();
    }

    public void decrementSession() {
        activeCartSessions.decrementAndGet();
    }

    @Lock(LockType.READ)
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Memory Metrics (Convert bytes to MB)
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        metrics.put("heapUsedMB", heapUsed);
        metrics.put("heapMaxMB", heapMax);
        
        // Thread Metrics
        metrics.put("activeThreads", threadBean.getThreadCount());
        metrics.put("peakThreads", threadBean.getPeakThreadCount());
        
        // Uptime (Milliseconds to descriptive string)
        long uptimeMs = runtimeBean.getUptime();
        long hours = (uptimeMs / (1000 * 60 * 60)) % 24;
        long minutes = (uptimeMs / (1000 * 60)) % 60;
        long seconds = (uptimeMs / 1000) % 60;
        metrics.put("uptime", String.format("%02dh %02dm %02ds", hours, minutes, seconds));
        
        // Custom EJB Metrics
        metrics.put("activeSessions", activeCartSessions.get());

        return metrics;
    }
}
