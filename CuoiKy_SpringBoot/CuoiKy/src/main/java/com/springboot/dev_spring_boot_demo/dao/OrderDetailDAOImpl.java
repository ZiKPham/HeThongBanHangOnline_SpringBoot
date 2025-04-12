package com.springboot.dev_spring_boot_demo.dao;

import com.springboot.dev_spring_boot_demo.entity.OrderDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDetailDAOImpl implements OrderDetailDAO {

    private EntityManager entityManager;

    @Autowired
    public OrderDetailDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<OrderDetail> findByOrderId(Integer orderId) {
        TypedQuery<OrderDetail> query = entityManager.createQuery(
                "from OrderDetail where order.id = :orderId", OrderDetail.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return entityManager.merge(orderDetail);
    }
} 