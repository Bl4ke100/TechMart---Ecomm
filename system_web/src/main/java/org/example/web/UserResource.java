package org.example.web;

import org.example.model.User;
import org.example.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@jakarta.ejb.Stateless
public class UserResource {

    @jakarta.ejb.EJB
    private UserService userService;

    @GET
    public Response getAllUsers(@HeaderParam("X-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"Access Denied\"}").build();
        }
        List<User> users = userService.getAllUsers();
        return Response.ok(users).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id, @HeaderParam("X-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"Access Denied\"}").build();
        }
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Response.ok("{\"message\":\"User deleted successfully\"}").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"User not found\"}").build();
    }

    @GET
    @Path("/self/{username}")
    public Response getSelf(@PathParam("username") String username, @HeaderParam("X-User") String requester) {
        if (!username.equals(requester)) return Response.status(Response.Status.FORBIDDEN).build();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            user.setPassword("");
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/self/{username}")
    public Response updateSelf(@PathParam("username") String username, @HeaderParam("X-User") String requester, User updatedUser) {
        if (!username.equals(requester)) return Response.status(Response.Status.FORBIDDEN).build();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            user.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }
            userService.updateUser(user);
            return Response.ok("{\"message\":\"Profile updated\"}").build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/self/{username}")
    public Response deleteSelf(@PathParam("username") String username, @HeaderParam("X-User") String requester) {
        if (!username.equals(requester)) return Response.status(Response.Status.FORBIDDEN).build();
        if (userService.deleteUserByUsername(username)) {
            return Response.ok("{\"message\":\"Account deleted\"}").build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
