package lk.blake.Components;

import lk.blake.model.Order;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/OrderTopic"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic")
})
public class AuditLogMDB implements MessageListener {

    private static final Logger logger = Logger.getLogger(AuditLogMDB.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMsg = (ObjectMessage) message;
                Order order = (Order) objMsg.getObject();
                
                // Perform non-durable, real-time audit logging
                logger.info("[AuditLogMDB - NonDurable] AUDIT EVENT: Order " + order.getOrderId() + 
                            " completed by " + order.getUsername() + 
                            ". Product ID: " + order.getProductId() + 
                            ", Quantity: " + order.getQuantity());
            }
        } catch (Exception e) {
            logger.severe("[AuditLogMDB] Error logging audit event: " + e.getMessage());
        }
    }
}
