package com.springboot.api.dao;

import com.springboot.api.entity.OrderDetail;
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
    public List<OrderDetail> findAll() {
        TypedQuery<OrderDetail> query = entityManager.createQuery("from OrderDetail", OrderDetail.class);
        return query.getResultList();
    }

    @Override
    public List<OrderDetail> findByOrderId(Integer orderId) {
        TypedQuery<OrderDetail> query = entityManager.createQuery(
                "from OrderDetail where order.id = :orderId", OrderDetail.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    @Override
    public OrderDetail findById(Integer id) {
        return entityManager.find(OrderDetail.class, id);
    }

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return entityManager.merge(orderDetail);
    }

    @Override
    public void deleteById(Integer id) {
        OrderDetail orderDetail = entityManager.find(OrderDetail.class, id);
        if (orderDetail != null) {
            entityManager.remove(orderDetail);
        }
    }
} 