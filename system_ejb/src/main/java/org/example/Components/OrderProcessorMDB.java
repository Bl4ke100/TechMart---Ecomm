package org.example.Components;

import org.example.model.Order;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.EJB;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import org.example.service.InventoryService;
import org.example.service.NotifyService;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/OrderQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class OrderProcessorMDB implements MessageListener {

    @EJB
    private InventoryService inventoryService;

    @EJB
    private NotifyService notifyService;

    @jakarta.persistence.PersistenceContext(unitName = "techmartPU")
    private jakarta.persistence.EntityManager em;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMsg = (ObjectMessage) message;
                Order order = (Order) objMsg.getObject();
                order.setStatus("PROCESSED");
                order.setOrderDate(new java.util.Date());
                
                System.out.println("[MDB] Processing Order ID: " + order.getOrderId() + " for user: " + order.getUsername());
                
                inventoryService.decreaseInventory(order.getProductId(), order.getQuantity());
                
                em.persist(order);
                System.out.println("[MDB] Payment processed and order persisted successfully.");
                
                notifyService.sendOrderConfirmation(order.getUsername(), order.getOrderId());
                notifyService.sendShippingUpdate(order.getUsername(), order.getOrderId(), "Processing");
                
                System.out.println("[MDB] Order processing completed.");
            }
        } catch (Exception e) {
            System.err.println("[MDB] Error processing order message:");
            e.printStackTrace();
        }
    }
}
