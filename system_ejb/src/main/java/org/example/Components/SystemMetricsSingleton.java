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
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.example.service.SystemMetricsService;

/**
 * Singleton Session Bean maneging global system telemetry.
 * Utalizes @Startup to eagerly load JMX beans into memory during server boot.
 * Employs thread-safe Atomic variables to track statistics linearly without 
 * locking bottlenecks, ensuring high scalability under heavy user concurrency.
 */
@Singleton
@Startup
public class SystemMetricsSingleton implements SystemMetricsService {

    private AtomicInteger activeCartSessions = new AtomicInteger(0);
    private AtomicInteger processedJmsMessages = new AtomicInteger(0);
    private AtomicInteger failedJmsMessages = new AtomicInteger(0);

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    private MemoryMXBean memoryBean;
    private ThreadMXBean threadBean;
    private RuntimeMXBean runtimeBean;
    private OperatingSystemMXBean osBean;

    @PostConstruct
    public void init() {
        memoryBean = ManagementFactory.getMemoryMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        osBean = ManagementFactory.getOperatingSystemMXBean();
    }

    public void incrementSession() {
        activeCartSessions.incrementAndGet();
    }

    public void decrementSession() {
        activeCartSessions.decrementAndGet();
    }

    public void incrementProcessedJmsMessage() {
        processedJmsMessages.incrementAndGet();
    }

    public void incrementFailedJmsMessage() {
        failedJmsMessages.incrementAndGet();
    }

    @Lock(LockType.READ)
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        metrics.put("heapUsedMB", heapUsed);
        metrics.put("heapMaxMB", heapMax);
        
        metrics.put("activeThreads", threadBean.getThreadCount());
        metrics.put("peakThreads", threadBean.getPeakThreadCount());
        
        long uptimeMs = runtimeBean.getUptime();
        long hours = (uptimeMs / (1000 * 60 * 60)) % 24;
        long minutes = (uptimeMs / (1000 * 60)) % 60;
        long seconds = (uptimeMs / 1000) % 60;
        metrics.put("uptime", String.format("%02dh %02dm %02ds", hours, minutes, seconds));
        
        metrics.put("activeSessions", activeCartSessions.get());
        
        metrics.put("cpuCores", osBean.getAvailableProcessors());
        metrics.put("systemCpuLoad", String.format("%.2f", osBean.getSystemLoadAverage()));

        metrics.put("jmsProcessed", processedJmsMessages.get());
        metrics.put("jmsFailed", failedJmsMessages.get());

        long dbStart = System.currentTimeMillis();
        try {
            Query q = em.createNativeQuery("SELECT 1");
            q.getSingleResult();
            long dbLatency = System.currentTimeMillis() - dbStart;
            metrics.put("dbLatencyMs", dbLatency);
            metrics.put("dbStatus", "OK");
        } catch (Exception e) {
            metrics.put("dbLatencyMs", -1);
            metrics.put("dbStatus", "ERROR");
        }

        return metrics;
    }
}
