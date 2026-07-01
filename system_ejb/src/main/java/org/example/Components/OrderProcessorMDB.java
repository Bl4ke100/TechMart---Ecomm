package org.example.Components;

import org.example.model.Order;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.EJB;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import org.example.service.InventoryService;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/OrderQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class OrderProcessorMDB implements MessageListener {

    @EJB
    private InventoryService inventoryService;

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "jms/OrderTopic", mappedName = "jms/OrderTopic")
    private Topic orderTopic;

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
                
                // Publish to Topic for decoupled processing
                System.out.println("[MDB] Publishing order to JMS Topic: jms/OrderTopic");
                jmsContext.createProducer().send(orderTopic, order);
                
                System.out.println("[MDB] Order processing completed.");
            }
        } catch (Exception e) {
            System.err.println("[MDB] Error processing order message:");
            e.printStackTrace();
        }
    }
}
