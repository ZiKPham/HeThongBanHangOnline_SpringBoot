package com.springboot.api.dao;

import com.springboot.api.entity.OrderDetail;
import java.util.List;

public interface OrderDetailDAO {
    List<OrderDetail> findAll();
    List<OrderDetail> findByOrderId(Integer orderId);
    OrderDetail findById(Integer id);
    OrderDetail save(OrderDetail orderDetail);
    void deleteById(Integer id);
} 