package lk.blake.test;

import lk.blake.service.OrderService;
import lk.blake.service.SystemMetricsService;
import lk.blake.Components.OrderServiceImpl;
import lk.blake.Components.SystemMetricsSingleton;
import lk.blake.model.Order;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.ejb.EJB;
import java.util.Map;

@ExtendWith(ArquillianExtension.class)
public class OrderServiceIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(OrderService.class, OrderServiceImpl.class, 
                            SystemMetricsService.class, SystemMetricsSingleton.class,
                            Order.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
    }

    @EJB
    private SystemMetricsService metricsService;

    @Test
    public void testMetricsInjectionAndExecution() {
        assertNotNull(metricsService, "Metrics Service should be injected by Arquillian");
        
        Map<String, Object> metrics = metricsService.getSystemMetrics();
        assertNotNull(metrics, "Metrics map should not be null");
        assertTrue(metrics.containsKey("cpuCores"), "Metrics should contain CPU cores");
        assertTrue(metrics.containsKey("systemCpuLoad"), "Metrics should contain System CPU Load");
        
        System.out.println("Integration Test Passed. CPU Cores: " + metrics.get("cpuCores"));
    }
}
