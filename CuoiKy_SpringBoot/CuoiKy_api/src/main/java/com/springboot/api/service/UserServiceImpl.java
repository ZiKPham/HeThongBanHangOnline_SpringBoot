package com.springboot.api.service;

import com.springboot.api.entity.User;
import com.springboot.api.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }
    
    @Override
    public User getUserByPhone(String phone) {
        return userDao.findByPhone(phone);
    }
    
    @Override
    @Transactional
    public User saveUser(User user) {
        return userDao.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(String username, User user) {
        User existingUser = userDao.findByUsername(username);
        if (existingUser == null) {
            return null;
        }
        
        user.setUsername(username);
        return userDao.save(user);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return userDao.existsByPhone(phone);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        userDao.deleteByUsername(username);
    }
} 