package org.example.Components;

import org.example.model.User;
import org.example.service.UserService;
import jakarta.ejb.Stateless;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
@Stateless
public class UserServiceImpl implements UserService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    @Override
    public User authenticate(String username, String password) {
        System.out.println("[AUTH DEBUG] Attempting to authenticate user: '" + username + "' with password: '" + password + "'");
        try {
            User u = em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .setHint("jakarta.persistence.cache.retrieveMode", jakarta.persistence.CacheRetrieveMode.BYPASS)
                     .getSingleResult();
            System.out.println("[AUTH DEBUG] SUCCESS! Found user in DB: ID=" + u.getId() + ", Role=" + u.getRole());
            return u;
        } catch (NoResultException e) {
            System.out.println("[AUTH DEBUG] FAILED: NoResultException thrown! The database returned 0 rows for username='" + username + "' and password='" + password + "'. Please check if the user physically exists in the techmartdb.users table with these exact credentials.");
            return null;
        } catch (Exception e) {
            System.out.println("[AUTH DEBUG] SEVERE EXCEPTION during authentication:");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    @Override
    public User registerUser(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public boolean deleteUser(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
            return true;
        }
        return false;
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                     .setParameter("username", username)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean updateUser(User user) {
        try {
            em.merge(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteUserByUsername(String username) {
        User user = getUserByUsername(username);
        if (user != null) {
            em.remove(user);
            return true;
        }
        return false;
    }
}
