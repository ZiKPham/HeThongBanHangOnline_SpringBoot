package com.springboot.api.rest;

import com.springboot.api.entity.Order;
import com.springboot.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private OrderService orderService;

    @Autowired
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{username}")
    public List<Order> findByUsername(@PathVariable String username) {
        return orderService.findByUsername(username);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            if (order.getOrderDate() == null) {
                order.setOrderDate(LocalDateTime.now());
            }
            
            if (order.getStatus() == null) {
                order.setStatus(Order.OrderStatus.PENDING);
            }
            
            Order savedOrder = orderService.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi tạo đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer orderId, @RequestBody Order order) {
        try {
            Order existingOrder = orderService.findById(orderId);
            if (existingOrder == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            order.setOrderId(orderId);
            Order updatedOrder = orderService.save(order);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi cập nhật đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null || newStatus.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Trạng thái đơn hàng không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Order existingOrder = orderService.findById(orderId);
            if (existingOrder == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            orderService.updateOrderStatus(orderId, newStatus);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cập nhật trạng thái đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
        try {
            Order existingOrder = orderService.findById(orderId);
            if (existingOrder == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            orderService.deleteById(orderId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi xóa đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
} 