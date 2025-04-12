package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.User;
import java.util.List;

public interface UserDAO {
    List<User> findAll();
    User findByUsername(String username);
    User save(User user);
    void deleteByUsername(String username);
    boolean existsByUsername(String username);
    void saveAuthority(String username, String authority);
} 