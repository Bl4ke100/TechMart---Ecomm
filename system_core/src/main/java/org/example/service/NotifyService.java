package org.example.service;

import jakarta.ejb.Remote;
import java.util.concurrent.Future;

@Remote
public interface NotifyService {
    Future<Boolean> sendOrderConfirmation(String username, int orderId);
    Future<Boolean> sendShippingUpdate(String username, int orderId, String status);
}
