package org.example.Components;

import org.example.model.Product;
import org.example.service.ProductService;
import org.example.service.InventoryService;
import jakarta.ejb.Stateless;
import jakarta.ejb.EJB;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProductServiceImpl implements ProductService {

    @jakarta.persistence.PersistenceContext(unitName = "techmartPU")
    private jakarta.persistence.EntityManager em;

    private Logger logger;

    @PostConstruct
    public void init() {
        logger = Logger.getLogger(ProductServiceImpl.class.getName());
        logger.info("[Lifecycle: @PostConstruct] ProductServiceImpl pooled instance created and DB context injected.");
    }

    @PreDestroy
    public void destroy() {
        logger.info("[Lifecycle: @PreDestroy] ProductServiceImpl pooled instance is being destroyed. Releasing resources.");
    }

    @Override
    public List<Product> getAllProducts() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    @Override
    public Product getProduct(int productId) {
        return em.find(Product.class, productId);
    }

    @Override
    public Product addProduct(Product product) {
        em.persist(product);
        em.flush();
        return product;
    }

    @Override
    public boolean updateProduct(Product product) {
        Product existing = em.find(Product.class, product.getId());
        if (existing != null) {
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            existing.setInventoryCount(product.getInventoryCount());
            em.merge(existing);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteProduct(int productId) {
        Product p = em.find(Product.class, productId);
        if (p != null) {
            em.remove(p);
            return true;
        }
        return false;
    }
}
