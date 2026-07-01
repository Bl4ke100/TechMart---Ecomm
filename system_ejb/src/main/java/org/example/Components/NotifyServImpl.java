package org.example.Components;

import org.example.service.NotifyService;
import jakarta.ejb.Stateless;
import jakarta.ejb.Asynchronous;

@Stateless
public class NotifyServImpl implements NotifyService {

    @Override
    @Asynchronous
    public void sendOrderConfirmation(String username, int orderId) {
        System.out.println("[Async Notification] Starting order confirmation process for User: " + username + ", Order ID: " + orderId);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[Async Notification] Order confirmation sent to " + username + " for Order ID: " + orderId);
    }

    @Override
    @Asynchronous
    public void sendShippingUpdate(String username, int orderId, String status) {
        System.out.println("[Async Notification] Starting shipping update process for User: " + username + ", Order ID: " + orderId + ", Status: " + status);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[Async Notification] Shipping update sent to " + username + " for Order ID: " + orderId + " - Status: " + status);
    }
}
