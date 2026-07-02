package lk.blake.Components;

import lk.blake.service.NotifyService;
import jakarta.ejb.Stateless;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.AsyncResult;
import java.util.concurrent.Future;

@Stateless
public class NotifyServImpl implements NotifyService {

    @Override
    @Asynchronous
    public Future<Boolean> sendOrderConfirmation(String username, int orderId) {
        System.out.println("[Async Notification] Starting order confirmation process for User: " + username + ", Order ID: " + orderId);
        try {
            // Simulate variable latency (could be fast, could hang)
            long sleepTime = (long) (Math.random() * 5000); 
            Thread.sleep(sleepTime);
            System.out.println("[Async Notification] Order confirmation sent to " + username + " for Order ID: " + orderId);
            return new AsyncResult<>(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Asynchronous
    public Future<Boolean> sendShippingUpdate(String username, int orderId, String status) {
        System.out.println("[Async Notification] Starting shipping update process for User: " + username + ", Order ID: " + orderId + ", Status: " + status);
        try {
            Thread.sleep(1000);
            System.out.println("[Async Notification] Shipping update sent to " + username + " for Order ID: " + orderId + " - Status: " + status);
            return new AsyncResult<>(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new AsyncResult<>(false);
        }
    }
}
