package org.example.service;

import org.example.model.Order;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface OrderService {
    void placeOrder(Order order);
    List<Order> getOrdersByUser(String username);
    List<Order> getAllOrders();
}
