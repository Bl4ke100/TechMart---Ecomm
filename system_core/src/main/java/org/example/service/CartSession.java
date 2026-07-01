package org.example.service;

import org.example.model.Product;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface CartSession {
    void addProduct(Product product, int quantity);
    void removeProduct(int productId);
    List<Product> getCartItems();
    void checkout(String username);
    void clearCart();
    void endSession();
}
