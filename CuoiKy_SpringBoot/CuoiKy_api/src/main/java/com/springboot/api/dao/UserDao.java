package com.springboot.api.dao;

import com.springboot.api.entity.User;
import java.util.List;

public interface UserDao {
    
    List<User> findAll();
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    User findByPhone(String phone);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    User save(User user);
    
    void deleteByUsername(String username);
} 