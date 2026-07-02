package lk.blake.web;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.blake.web.dto.LoginRequestDTO;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@jakarta.ejb.Stateless
public class AuthResource {

    @jakarta.ejb.EJB
    private lk.blake.service.UserService userService;

    @POST
    public Response login(LoginRequestDTO request) {
        try {
            lk.blake.model.User user = userService.authenticate(request.getUsername(), request.getPassword());
            if (user != null) {
                return Response.ok().entity("{\"token\": \"fake-jwt-token\", \"username\": \"" + user.getUsername() + "\", \"role\": \"" + user.getRole() + "\"}").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Invalid credentials\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Backend Error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/register")
    public Response register(lk.blake.model.User user) {
        try {
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            userService.registerUser(user);
            return Response.ok().entity("{\"message\": \"Registration successful\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Registration failed or username exists\"}").build();
        }
    }
}
