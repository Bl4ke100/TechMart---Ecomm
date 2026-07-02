package org.example.test;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Logger;

/**
 * Performance Testing Suite to satisfy assignment requirements.
 * This class isolates performance-critical components and runs benchmarking
 * loops to calculate average execution times.
 */
public class PerformanceBenchmarkTest {

    private static final Logger logger = Logger.getLogger(PerformanceBenchmarkTest.class.getName());

    @RepeatedTest(5)
    public void benchmarkObjectInstantiation() {
        long start = System.nanoTime();
        
        // Simulate a rapid burst of 10,000 order allocations (NFR: Scalability)
        for (int i = 0; i < 10000; i++) {
            org.example.model.Order order = new org.example.model.Order();
            order.setQuantity(i);
        }
        
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        logger.info("Allocated 10,000 objects in: " + durationMs + " ms");
        
        // Performance Requirement: Must be able to allocate 10k entities in under 150ms
        assertTrue(durationMs < 150, "Performance bottleneck detected: High allocation latency");
    }

    @Test
    public void benchmarkStringManipulationForJson() {
        long start = System.nanoTime();
        
        // Simulate complex string building for REST responses
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            sb.append("{\"id\":").append(i).append(", \"status\":\"PROCESSED\"},");
        }
        String result = sb.toString();
        
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        logger.info("JSON serialization simulation completed in: " + durationMs + " ms");
        
        assertTrue(durationMs < 100, "Performance bottleneck detected: JSON building is too slow");
        assertTrue(result.length() > 0);
    }
}
