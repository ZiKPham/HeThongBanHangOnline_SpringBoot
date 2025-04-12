package com.springboot.api.dao;

import com.springboot.api.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {

    private EntityManager entityManager;

    @Autowired
    public OrderDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Order> findAll() {
        TypedQuery<Order> query = entityManager.createQuery("from Order", Order.class);
        return query.getResultList();
    }

    @Override
    public Order findById(Integer id) {
        return entityManager.find(Order.class, id);
    }

    @Override
    public List<Order> findByUsername(String username) {
        TypedQuery<Order> query = entityManager.createQuery(
                "from Order where username = :username", Order.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public Order save(Order order) {
        return entityManager.merge(order);
    }

    @Override
    public void deleteById(Integer id) {
        Order order = entityManager.find(Order.class, id);
        if (order != null) {
            entityManager.remove(order);
        }
    }

    @Override
    public void updateOrderStatus(Integer orderId, String status) {
        Order order = findById(orderId);
        if (order != null) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                order.setStatus(orderStatus);
                entityManager.merge(order);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Trạng thái đơn hàng không hợp lệ: " + status);
            }
        }
    }
} 