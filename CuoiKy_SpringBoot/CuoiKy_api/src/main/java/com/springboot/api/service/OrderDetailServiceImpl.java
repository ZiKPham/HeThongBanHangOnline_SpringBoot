package com.springboot.api.service;

import com.springboot.api.dao.OrderDetailDAO;
import com.springboot.api.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private OrderDetailDAO orderDetailDAO;

    @Autowired
    public OrderDetailServiceImpl(OrderDetailDAO orderDetailDAO) {
        this.orderDetailDAO = orderDetailDAO;
    }

    @Override
    public List<OrderDetail> findAll() {
        return orderDetailDAO.findAll();
    }

    @Override
    public List<OrderDetail> findByOrderId(Integer orderId) {
        return orderDetailDAO.findByOrderId(orderId);
    }

    @Override
    public OrderDetail findById(Integer id) {
        return orderDetailDAO.findById(id);
    }

    @Override
    @Transactional
    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailDAO.save(orderDetail);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        orderDetailDAO.deleteById(id);
    }
} 