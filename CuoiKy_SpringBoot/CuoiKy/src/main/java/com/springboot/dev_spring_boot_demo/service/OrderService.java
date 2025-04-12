package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Integer id);
    List<Order> findByUsername(String username);
    Order save(Order order);
    void deleteById(Integer id);
    Order createOrderFromCart(String username);
    Order updateOrderStatus(Integer orderId, String status);
} 