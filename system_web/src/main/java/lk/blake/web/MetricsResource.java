package lk.blake.web;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.blake.service.SystemMetricsService;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
@jakarta.ejb.Stateless
public class MetricsResource {

    @EJB
    private SystemMetricsService metricsSingleton;

    @GET
    public Response getSystemMetrics(@HeaderParam("X-Role") String role) {
        // Strict security: Only ADMIN can view internal system health
        if (!"ADMIN".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"Access Denied\"}").build();
        }
        
        try {
            return Response.ok(metricsSingleton.getSystemMetrics()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
