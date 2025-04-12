package com.springboot.api.service;

import com.springboot.api.dao.AuthorityDAO;
import com.springboot.api.entity.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private AuthorityDAO authorityDAO;

    @Autowired
    public AuthorityServiceImpl(AuthorityDAO authorityDAO) {
        this.authorityDAO = authorityDAO;
    }

    @Override
    public List<Authority> findAll() {
        return authorityDAO.findAll();
    }

    @Override
    public List<Authority> findByUsername(String username) {
        return authorityDAO.findByUsername(username);
    }

    @Override
    public Authority findById(String username) {
        return authorityDAO.findById(username);
    }

    @Override
    @Transactional
    public Authority save(Authority authority) {
        return authorityDAO.save(authority);
    }

    @Override
    @Transactional
    public void deleteById(String username) {
        authorityDAO.deleteById(username);
    }
} 