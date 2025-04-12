package com.springboot.api.dao;

import com.springboot.api.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

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
    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
            "from User where email = :email", User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findByPhone(String phone) {
        TypedQuery<User> query = entityManager.createQuery(
            "from User where phone = :phone", User.class);
        query.setParameter("phone", phone);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
            "select count(u) from User u where u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "select count(u) from User u where u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        TypedQuery<Long> query = entityManager.createQuery(
            "select count(u) from User u where u.phone = :phone", Long.class);
        query.setParameter("phone", phone);
        return query.getSingleResult() > 0;
    }

    @Override
    public User save(User user) {
        try {
            User existingUser = findByUsername(user.getUsername());
            if (existingUser == null) {
                entityManager.persist(user);
                return user;
            } else {
                return entityManager.merge(user);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi lưu user: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteByUsername(String username) {
        User user = findByUsername(username);
        if (user != null) {
            entityManager.remove(user);
        }
    }
} 