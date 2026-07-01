package org.example.web;

import org.example.model.Product;
import org.example.service.ProductService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@jakarta.ejb.Stateless
public class ProductResource {

    @EJB
    private ProductService productService;

    @GET
    public jakarta.ws.rs.core.Response getAllProducts() {
        return jakarta.ws.rs.core.Response.ok(productService.getAllProducts()).build();
    }

    @GET
    @Path("/{id}")
    public jakarta.ws.rs.core.Response getProduct(@PathParam("id") int id) {
        Product p = productService.getProduct(id);
        if (p == null) {
            return jakarta.ws.rs.core.Response.status(404).build();
        }
        return jakarta.ws.rs.core.Response.ok(p).build();
    }

    @POST
    public jakarta.ws.rs.core.Response addProduct(Product product, @HeaderParam("X-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.FORBIDDEN).build();
        }
        Product created = productService.addProduct(product);
        return jakarta.ws.rs.core.Response.ok(created).build();
    }

    @PUT
    @Path("/{id}")
    public jakarta.ws.rs.core.Response updateProduct(@PathParam("id") int id, Product product, @HeaderParam("X-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.FORBIDDEN).build();
        }
        product.setId(id);
        boolean updated = productService.updateProduct(product);
        if (updated) {
            return jakarta.ws.rs.core.Response.ok("{\"message\":\"Product updated\"}").build();
        }
        return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public jakarta.ws.rs.core.Response deleteProduct(@PathParam("id") int id, @HeaderParam("X-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.FORBIDDEN).build();
        }
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            java.io.File dir = new java.io.File("d:/Uni Stuff/BCD/auction_system/auction_system/product-images/" + id);
            if (dir.exists()) {
                java.io.File[] files = dir.listFiles();
                if (files != null) {
                    for (java.io.File f : files) f.delete();
                }
                dir.delete();
            }
            return jakarta.ws.rs.core.Response.ok("{\"message\":\"Product deleted\"}").build();
        }
        return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.NOT_FOUND).build();
    }
}
