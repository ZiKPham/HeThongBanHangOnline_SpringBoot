package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.Order;
import java.util.List;

public interface OrderDAO {
    List<Order> findAll();
    Order findById(Integer id);
    List<Order> findByUsername(String username);
    Order save(Order order);
    void deleteById(Integer id);
} 