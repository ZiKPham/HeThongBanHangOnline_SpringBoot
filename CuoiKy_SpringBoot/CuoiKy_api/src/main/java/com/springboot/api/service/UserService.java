package com.springboot.api.service;

import com.springboot.api.entity.User;
import java.util.List;

public interface UserService {
    
    List<User> getAllUsers();
    
    User getUserByUsername(String username);
    
    User getUserByEmail(String email);
    
    User getUserByPhone(String phone);
    
    User saveUser(User user);
    
    User updateUser(String username, User user);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    void deleteUser(String username);
} 