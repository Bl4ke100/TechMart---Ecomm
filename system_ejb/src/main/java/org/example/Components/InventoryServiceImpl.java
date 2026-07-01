package org.example.Components;

import org.example.service.InventoryService;
import jakarta.ejb.Singleton;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Startup;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class InventoryServiceImpl implements InventoryService {

    @jakarta.persistence.PersistenceContext(unitName = "techmartPU")
    private jakarta.persistence.EntityManager em;

    private Logger logger;
    private long startupTime;

    public InventoryServiceImpl() {
    }

    @PostConstruct
    public void onStartup() {
        logger = Logger.getLogger(InventoryServiceImpl.class.getName());
        startupTime = System.currentTimeMillis();
        logger.info("[Lifecycle: @PostConstruct] InventoryServiceImpl Singleton started. System cache warming up...");
    }

    @PreDestroy
    public void onShutdown() {
        long uptime = System.currentTimeMillis() - startupTime;
        logger.info("[Lifecycle: @PreDestroy] InventoryServiceImpl Singleton shutting down. System uptime: " + uptime + " ms. Flushing caches.");
    }

    @Override
    @Lock(LockType.WRITE)
    public void syncInventory() {
        System.out.println("Synchronizing inventory across databases...");
    }

    @Override
    @Lock(LockType.READ)
    public boolean checkAvailability(int productId, int quantity) {
        org.example.model.Product p = em.find(org.example.model.Product.class, productId);
        return p != null && p.getInventoryCount() >= quantity;
    }

    @Override
    @Lock(LockType.WRITE)
    public void decreaseInventory(int productId, int quantity) {
        org.example.model.Product p = em.find(org.example.model.Product.class, productId);
        if (p != null && p.getInventoryCount() >= quantity) {
            p.setInventoryCount(p.getInventoryCount() - quantity);
            em.merge(p);
            System.out.println("Decreased inventory for product " + productId + " by " + quantity);
        } else {
            throw new RuntimeException("Insufficient inventory for product " + productId);
        }
    }
}
