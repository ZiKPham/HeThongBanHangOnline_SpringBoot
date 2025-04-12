package com.springboot.api.service;

import com.springboot.api.dao.OrderDAO;
import com.springboot.api.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderDAO orderDAO;

    @Autowired
    public OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public List<Order> findAll() {
        return orderDAO.findAll();
    }

    @Override
    public Order findById(Integer id) {
        return orderDAO.findById(id);
    }

    @Override
    public List<Order> findByUsername(String username) {
        return orderDAO.findByUsername(username);
    }

    @Override
    @Transactional
    public Order save(Order order) {
        return orderDAO.save(order);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        orderDAO.deleteById(id);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer orderId, String status) {
        orderDAO.updateOrderStatus(orderId, status);
    }
} 