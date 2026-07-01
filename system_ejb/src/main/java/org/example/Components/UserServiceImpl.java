package org.example.Components;

import org.example.model.User;
import org.example.service.UserService;
import jakarta.ejb.Stateless;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import jakarta.annotation.sql.DataSourceDefinition;

@DataSourceDefinition(
    name = "java:global/jdbc/techmartDB",
    className = "com.mysql.cj.jdbc.MysqlDataSource",
    user = "root",
    password = "1234",
    url = "jdbc:mysql://localhost:3306/techmartdb?useSSL=false&allowPublicKeyRetrieval=true"
)
@Stateless
public class UserServiceImpl implements UserService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    @Override
    public User authenticate(String username, String password) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
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
