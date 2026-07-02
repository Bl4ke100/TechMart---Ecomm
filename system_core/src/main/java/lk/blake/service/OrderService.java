package lk.blake.service;

import lk.blake.model.Order;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface OrderService {
    void placeOrder(Order order);
    List<Order> getOrdersByUser(String username);
    List<Order> getAllOrders();
}
