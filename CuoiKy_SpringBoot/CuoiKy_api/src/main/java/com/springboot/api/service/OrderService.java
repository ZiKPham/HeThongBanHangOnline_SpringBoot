package com.springboot.api.service;

import com.springboot.api.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Integer id);
    List<Order> findByUsername(String username);
    Order save(Order order);
    void deleteById(Integer id);
    void updateOrderStatus(Integer orderId, String status);
} 