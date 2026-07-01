package org.example.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    @GET
    public jakarta.ws.rs.core.Response getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        metrics.put("systemLoadAverage", osBean.getSystemLoadAverage());
        metrics.put("availableProcessors", osBean.getAvailableProcessors());
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        metrics.put("maxMemoryMB", maxMemory / (1024 * 1024));
        metrics.put("allocatedMemoryMB", allocatedMemory / (1024 * 1024));
        metrics.put("freeMemoryMB", freeMemory / (1024 * 1024));
        metrics.put("usedMemoryMB", (allocatedMemory - freeMemory) / (1024 * 1024));
        
        return jakarta.ws.rs.core.Response.ok(metrics).build();
    }
}
