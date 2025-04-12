package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.OrderDetail;
import java.util.List;

public interface OrderDetailDAO {
    List<OrderDetail> findByOrderId(Integer orderId);
    OrderDetail save(OrderDetail orderDetail);
} 