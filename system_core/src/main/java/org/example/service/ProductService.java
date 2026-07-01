package org.example.service;

import org.example.model.Product;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface ProductService {
    List<Product> getAllProducts();
    Product getProduct(int productId);
    Product addProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int productId);
}
