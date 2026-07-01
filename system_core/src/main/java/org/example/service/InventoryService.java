package org.example.service;

import jakarta.ejb.Remote;

@Remote
public interface InventoryService {
    void syncInventory();
    boolean checkAvailability(int productId, int quantity);
    void decreaseInventory(int productId, int quantity);
}
