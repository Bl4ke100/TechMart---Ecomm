package org.example.Components;

import org.example.service.InventoryService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;

@Singleton
@Startup
public class JNDIPerformanceMonitor {

    private static final Logger logger = Logger.getLogger(JNDIPerformanceMonitor.class.getName());

    // 1. Dependency Injection approach (Container handled)
    @EJB
    private InventoryService injectedInventoryService;

    @PostConstruct
    public void monitorLookupPerformance() {
        logger.info("[JNDI Monitor] Starting JNDI Lookup vs @EJB Injection Performance Analysis...");

        // Measure Injection execution (Already initialized by container)
        long injectStart = System.nanoTime();
        boolean isInjectReady = (injectedInventoryService != null);
        long injectDuration = System.nanoTime() - injectStart;

        // Measure Manual JNDI Lookup execution
        long lookupDuration = 0;
        boolean isLookupReady = false;
        try {
            long lookupStart = System.nanoTime();
            InitialContext ctx = new InitialContext();
            // Look up the InventoryService using portable JNDI name
            InventoryService lookedUpService = (InventoryService) ctx.lookup("java:module/InventoryServiceImpl");
            isLookupReady = (lookedUpService != null);
            lookupDuration = System.nanoTime() - lookupStart;
        } catch (NamingException e) {
            logger.severe("[JNDI Monitor] JNDI Lookup failed: " + e.getMessage());
        }

        logger.info("[JNDI Monitor] --- PERFORMANCE RESULTS ---");
        logger.info(String.format("[JNDI Monitor] @EJB Injection Resolution Time: %,d nanoseconds", injectDuration));
        logger.info(String.format("[JNDI Monitor] Manual JNDI Lookup Time:     %,d nanoseconds", lookupDuration));
        logger.info("[JNDI Monitor] Analysis: Dependency Injection (@EJB) is magnitudes faster at runtime because the container pre-resolves the JNDI tree during bean instantiation. Manual lookups incur severe overhead (parsing the namespace, object factory delegation) and should be avoided in high-throughput enterprise paths unless absolutely necessary.");
    }
}
