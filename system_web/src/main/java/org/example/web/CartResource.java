package org.example.web;

import org.example.model.Product;
import org.example.service.CartSession;
import org.example.service.ProductService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.naming.InitialContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@jakarta.ejb.EJB(name = "ejb/CartSession", beanInterface = CartSession.class)
public class CartResource {

    private static final Map<String, CartSession> sessionMap = new ConcurrentHashMap<>();

    private CartSession getSession(String username) throws Exception {
        if (!sessionMap.containsKey(username)) {
            InitialContext ctx = new InitialContext();
            CartSession cart = (CartSession) ctx.lookup("java:comp/env/ejb/CartSession");
            sessionMap.put(username, cart);
        }
        return sessionMap.get(username);
    }

    @POST
    @Path("/add")
    public Response addToCart(@HeaderParam("X-User") String username, @QueryParam("quantity") int quantity, Product product) {
        try {
            CartSession cart = getSession(username);
            cart.addProduct(product, quantity > 0 ? quantity : 1);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to add to cart").build();
        }
    }

    @DELETE
    @Path("/{productId}")
    public Response removeFromCart(@HeaderParam("X-User") String username, @PathParam("productId") int productId) {
        try {
            CartSession cart = getSession(username);
            cart.removeProduct(productId);
            return Response.ok("Item removed from cart").build();
        } catch (Exception e) {
            return Response.serverError().entity("Failed to remove item").build();
        }
    }

    @GET
    public Response getCart(@HeaderParam("X-User") String username) {
        try {
            CartSession cart = getSession(username);
            List<Product> items = cart.getCartItems();
            return Response.ok(items).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/checkout")
    public Response checkout(@HeaderParam("X-User") String username) {
        try {
            CartSession cart = getSession(username);
            cart.checkout(username);
            return Response.ok("Checkout successful").build();
        } catch (Exception e) {
            return Response.serverError().entity("Checkout failed").build();
        }
    }
}
