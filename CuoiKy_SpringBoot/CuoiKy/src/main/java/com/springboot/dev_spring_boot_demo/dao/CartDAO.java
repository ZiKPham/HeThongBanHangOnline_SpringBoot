package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartDAO extends JpaRepository<Cart, Integer> {
    // Chỉ cần giữ lại các phương thức tùy chỉnh
    List<Cart> findByUsername(String username);
    List<Cart> findByUsernameAndProductId(String username, Integer productId);
    void deleteByUsername(String username);
} 