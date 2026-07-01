package org.example.service;

import org.example.model.User;
import java.util.List;
import jakarta.ejb.Local;

@Local
public interface UserService {
    User authenticate(String username, String password);
    User registerUser(User user);
    List<User> getAllUsers();
    boolean deleteUser(int id);
    User getUserByUsername(String username);
    boolean updateUser(User user);
    boolean deleteUserByUsername(String username);
}
