package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.CartDAO;
import com.springboot.dev_spring_boot_demo.dao.OrderDAO;
import com.springboot.dev_spring_boot_demo.dao.OrderDetailDAO;
import com.springboot.dev_spring_boot_demo.dao.ProductDAO;
import com.springboot.dev_spring_boot_demo.entity.Cart;
import com.springboot.dev_spring_boot_demo.entity.Order;
import com.springboot.dev_spring_boot_demo.entity.OrderDetail;
import com.springboot.dev_spring_boot_demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final OrderDetailDAO orderDetailDAO;
    private final CartDAO cartDAO;
    private final CartService cartService;
    private final ProductDAO productDAO;

    @Autowired
    public OrderServiceImpl(OrderDAO orderDAO, OrderDetailDAO orderDetailDAO, 
                           CartDAO cartDAO, CartService cartService, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.orderDetailDAO = orderDetailDAO;
        this.cartDAO = cartDAO;
        this.cartService = cartService;
        this.productDAO = productDAO;
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
    public Order save(Order order) {
        return orderDAO.save(order);
    }

    @Override
    public void deleteById(Integer id) {
        orderDAO.deleteById(id);
    }

    @Override
    @Transactional
    public Order createOrderFromCart(String username) {
        // Lấy giỏ hàng của user
        List<Cart> cartItems = cartDAO.findByUsername(username);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Kiểm tra số lượng tồn kho trước khi tạo đơn hàng
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " chỉ còn " + product.getStockQuantity() + " sản phẩm");
            }
        }

        // Tính tổng tiền
        Long totalAmount = cartService.getCartTotal(username);

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUsername(username);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING.name());

        // Lưu đơn hàng
        order = orderDAO.save(order);

        // Tạo chi tiết đơn hàng từ giỏ hàng và cập nhật số lượng tồn kho
        for (Cart cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            
            // Cập nhật số lượng tồn kho
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productDAO.save(product);
            
            orderDetailDAO.save(orderDetail);
        }

        // Xóa giỏ hàng sau khi đặt hàng
        cartService.clearCart(username);

        return order;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Integer orderId, String status) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }
        
        order.setStatus(status);
        return orderDAO.save(order);
    }
} 