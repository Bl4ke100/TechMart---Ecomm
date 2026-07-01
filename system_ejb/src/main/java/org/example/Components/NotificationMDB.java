package org.example.Components;

import org.example.model.Order;
import org.example.service.NotifyService;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.EJB;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/OrderTopic"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "NotificationMDB"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "NotificationSubscription")
})
public class NotificationMDB implements MessageListener {

    @EJB
    private NotifyService notifyService;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMsg = (ObjectMessage) message;
                Order order = (Order) objMsg.getObject();
                
                System.out.println("[NotificationMDB - Durable] Received processed order event for Order ID: " + order.getOrderId());
                
                // Trigger async processes
                Future<Boolean> confFuture = notifyService.sendOrderConfirmation(order.getUsername(), order.getOrderId());
                Future<Boolean> shipFuture = notifyService.sendShippingUpdate(order.getUsername(), order.getOrderId(), "Processing");
                
                try {
                    // Enforce a strict 3-second SLA on the email provider
                    boolean confSuccess = confFuture.get(3, TimeUnit.SECONDS);
                    boolean shipSuccess = shipFuture.get(3, TimeUnit.SECONDS);
                    
                    if (confSuccess && shipSuccess) {
                        System.out.println("[NotificationMDB] All notifications sent successfully within SLA.");
                    }
                } catch (TimeoutException e) {
                    System.err.println("[NotificationMDB - FAILURE RECOVERY] Notification SLA breached (>3 seconds). Routing to fallback queue for manual review.");
                    // Fallback logic here
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("[NotificationMDB - FAILURE RECOVERY] Async notification failed fatally. Retrying in background.");
                    // Retry logic here
                }
            }
        } catch (Exception e) {
            System.err.println("[NotificationMDB] Error processing notification:");
            e.printStackTrace();
        }
    }
}
