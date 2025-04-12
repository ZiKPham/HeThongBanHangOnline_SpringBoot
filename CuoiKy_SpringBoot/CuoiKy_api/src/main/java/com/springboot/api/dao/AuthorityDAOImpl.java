package com.springboot.api.dao;

import com.springboot.api.entity.Authority;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorityDAOImpl implements AuthorityDAO {

    private EntityManager entityManager;

    @Autowired
    public AuthorityDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Authority> findAll() {
        TypedQuery<Authority> query = entityManager.createQuery("from Authority", Authority.class);
        return query.getResultList();
    }

    @Override
    public List<Authority> findByUsername(String username) {
        TypedQuery<Authority> query = entityManager.createQuery(
                "from Authority where username = :username", Authority.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public Authority findById(String username) {
        return entityManager.find(Authority.class, username);
    }

    @Override
    public Authority save(Authority authority) {
        return entityManager.merge(authority);
    }

    @Override
    public void deleteById(String username) {
        Authority authority = entityManager.find(Authority.class, username);
        if (authority != null) {
            entityManager.remove(authority);
        }
    }
}