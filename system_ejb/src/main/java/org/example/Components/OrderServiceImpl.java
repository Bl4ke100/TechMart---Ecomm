package org.example.Components;

import org.example.model.Order;
import org.example.service.OrderService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.annotation.Resource;
import jakarta.jms.JMSProducer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OrderServiceImpl implements OrderService {

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "jms/OrderQueue", mappedName = "jms/OrderQueue")
    private Queue orderQueue;

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    @Override
    public void placeOrder(Order order) {
        System.out.println("Publishing order to JMS Queue for async processing: Order ID " + order.getOrderId());
        
        try {
            JMSProducer producer = jmsContext.createProducer();
            producer.send(orderQueue, order);
            System.out.println("Order successfully published to Queue.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to publish order to JMS queue", e);
        }
    }

    @Override
    public List<Order> getOrdersByUser(String username) {
        return em.createQuery("SELECT o FROM Order o WHERE o.username = :username ORDER BY o.orderDate DESC", Order.class)
                 .setParameter("username", username)
                 .setHint("jakarta.persistence.cache.retrieveMode", jakarta.persistence.CacheRetrieveMode.BYPASS)
                 .getResultList();
    }

    @Override
    public List<Order> getAllOrders() {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class)
                 .setHint("jakarta.persistence.cache.retrieveMode", jakarta.persistence.CacheRetrieveMode.BYPASS)
                 .getResultList();
    }
}
