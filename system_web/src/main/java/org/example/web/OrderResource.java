package org.example.web;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.service.OrderService;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@jakarta.ejb.Stateless
public class OrderResource {

    @EJB
    private OrderService orderService;

    @GET
    public Response getUserOrders(@HeaderParam("X-User") String username) {
        if (username == null || username.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(orderService.getOrdersByUser(username)).build();
    }

    @GET
    @Path("/all")
    public Response getAllOrders(@HeaderParam("X-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(orderService.getAllOrders()).build();
    }
}
