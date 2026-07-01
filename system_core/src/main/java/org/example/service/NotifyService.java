package org.example.service;

import jakarta.ejb.Remote;

@Remote
public interface NotifyService {
    void sendOrderConfirmation(String username, int orderId);
    void sendShippingUpdate(String username, int orderId, String status);
}
