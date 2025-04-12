package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {
    private EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        return query.getResultList();
    }

    @Override
    public User findByUsername(String username) {
        return entityManager.find(User.class, username);
    }

    @Override
    @Transactional
    public User save(User user) {
        return entityManager.merge(user);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        User user = entityManager.find(User.class, username);
        if (user != null) {
            entityManager.remove(user);
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
            query.setParameter("username", username);
            return query.getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public void saveAuthority(String username, String authority) {
        // Nếu bạn đang sử dụng JPA Entity cho Authority
        entityManager.createNativeQuery(
            "INSERT INTO authorities (username, authority) VALUES (?, ?)")
            .setParameter(1, username)
            .setParameter(2, authority)
            .executeUpdate();
    }
} 