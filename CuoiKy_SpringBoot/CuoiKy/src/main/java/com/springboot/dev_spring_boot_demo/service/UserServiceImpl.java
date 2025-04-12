package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.UserDAO;
import com.springboot.dev_spring_boot_demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userDAO.save(user);
    }

    @Override
    public void deleteByUsername(String username) {
        userDAO.deleteByUsername(username);
    }

    @Override
    public boolean registerUser(User user) {
        // Kiểm tra xem user đã tồn tại chưa
        if (userDAO.existsByUsername(user.getUsername())) {
            return false;
        }
        
        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        if (!encodedPassword.startsWith("{bcrypt}")) {
            encodedPassword = "{bcrypt}" + encodedPassword;
        }
        user.setPassword(encodedPassword);
        
        // Lưu user
        user.setEnabled(true);
        userDAO.save(user);
        
        // Lưu quyền
        userDAO.saveAuthority(user.getUsername(), "ROLE_USER");
        
        return true;
    }
} 