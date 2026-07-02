package org.example.service;

import jakarta.ejb.Local;
import java.util.Map;

@Local
public interface SystemMetricsService {
    void incrementSession();
    void decrementSession();
    void incrementProcessedJmsMessage();
    void incrementFailedJmsMessage();
    Map<String, Object> getSystemMetrics();
}
