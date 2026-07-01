package org.example.Components;

import org.example.model.Product;
import org.example.model.Order;
import org.example.service.CartSession;
import org.example.service.OrderService;
import jakarta.ejb.Stateful;
import jakarta.ejb.EJB;
import jakarta.ejb.Remove;
import jakarta.ejb.PostActivate;
import jakarta.ejb.PrePassivate;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Stateful
public class CartSessionImpl implements CartSession {

    private List<Product> cartItems = new ArrayList<>();
    private transient Logger logger; // transient so it doesn't get serialized during passivation
    private long sessionStartTime;
    
    @EJB
    private OrderService orderService;

    @PostConstruct
    public void init() {
        logger = Logger.getLogger(CartSessionImpl.class.getName());
        sessionStartTime = System.currentTimeMillis();
        logger.info("[Lifecycle: @PostConstruct] CartSession initialized for optimal performance.");
    }

    @PrePassivate
    public void passivate() {
        logger.info("[Lifecycle: @PrePassivate] Passivating CartSession to conserve memory. Items in cart: " + cartItems.size());
        // Clean up any non-serializable or temporary resources here
    }

    @PostActivate
    public void activate() {
        logger = Logger.getLogger(CartSessionImpl.class.getName());
        logger.info("[Lifecycle: @PostActivate] CartSession activated from disk. Restoring session state.");
    }

    @Remove
    @Override
    public void endSession() {
        long duration = System.currentTimeMillis() - sessionStartTime;
        logger.info("[Lifecycle: @Remove] Destroying CartSession. Session lasted " + duration + " ms.");
        cartItems.clear();
    }

    @Override
    public void addProduct(Product product, int quantity) {
        System.out.println("Adding product to cart: " + product.getName() + " (x" + quantity + ")");
        for(int i=0; i<quantity; i++) {
            cartItems.add(product);
        }
    }

    @Override
    public void removeProduct(int productId) {
        cartItems.removeIf(p -> p.getId() == productId);
    }

    @Override
    public List<Product> getCartItems() {
        return cartItems;
    }

    @Override
    public void checkout(String username) {
        System.out.println("User " + username + " is checking out " + cartItems.size() + " items.");
        
        Random rand = new Random();
        for (Product item : cartItems) {
            Order order = new Order();
            order.setOrderId(rand.nextInt(10000));
            order.setProductId(item.getId());
            order.setUsername(username);
            order.setQuantity(1);
            order.setOrderDate(new Date());
            order.setStatus("Pending");
            
            orderService.placeOrder(order);
        }
        
        clearCart();
    }

    @Override
    public void clearCart() {
        cartItems.clear();
    }
}
