package com.springboot.api.rest;

import com.springboot.api.entity.Order;
import com.springboot.api.entity.OrderDetail;
import com.springboot.api.entity.Product;
import com.springboot.api.service.OrderDetailService;
import com.springboot.api.service.OrderService;
import com.springboot.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailRestController {

    private OrderDetailService orderDetailService;
    private OrderService orderService;
    private ProductService productService;

    @Autowired
    public OrderDetailRestController(OrderDetailService orderDetailService, 
                                     OrderService orderService,
                                     ProductService productService) {
        this.orderDetailService = orderDetailService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrderDetails() {
        try {
            // Lấy tất cả chi tiết đơn hàng
            List<OrderDetail> allOrderDetails = orderDetailService.findAll();
            return ResponseEntity.ok(allOrderDetails);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi lấy danh sách chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Integer orderId) {
        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{orderDetailId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Integer orderDetailId) {
        try {
            OrderDetail orderDetail = orderDetailService.findById(orderDetailId);
            if (orderDetail == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy chi tiết đơn hàng với ID: " + orderDetailId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            return ResponseEntity.ok(orderDetail);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrderDetail(@RequestBody Map<String, Object> payload) {
        try {
            // Lấy thông tin từ payload
            Integer orderId = (Integer) payload.get("orderId");
            Integer productId = (Integer) payload.get("productId");
            Integer quantity = (Integer) payload.get("quantity");
            Long price = Long.valueOf(payload.get("price").toString());
            
            // Kiểm tra đơn hàng
            Order order = orderService.findById(orderId);
            if (order == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Kiểm tra sản phẩm
            Product product = productService.getProductById(productId.longValue());
            if (product == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy sản phẩm với ID: " + productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Tạo chi tiết đơn hàng mới
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setPrice(price);
            
            // Lưu chi tiết đơn hàng
            OrderDetail savedOrderDetail = orderDetailService.save(orderDetail);
            
            // Cập nhật tổng tiền đơn hàng
            updateOrderTotalAmount(order);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderDetail);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi tạo chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{orderDetailId}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable Integer orderDetailId, @RequestBody Map<String, Object> payload) {
        try {
            // Kiểm tra chi tiết đơn hàng
            OrderDetail existingOrderDetail = orderDetailService.findById(orderDetailId);
            if (existingOrderDetail == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy chi tiết đơn hàng với ID: " + orderDetailId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Cập nhật thông tin chi tiết đơn hàng
            Integer quantity = (Integer) payload.get("quantity");
            if (quantity != null) {
                existingOrderDetail.setQuantity(quantity);
            }
            
            Long price = payload.get("price") != null ? Long.valueOf(payload.get("price").toString()) : null;
            if (price != null) {
                existingOrderDetail.setPrice(price);
            }
            
            // Lưu chi tiết đơn hàng
            OrderDetail updatedOrderDetail = orderDetailService.save(existingOrderDetail);
            
            // Cập nhật tổng tiền đơn hàng
            updateOrderTotalAmount(existingOrderDetail.getOrder());
            
            return ResponseEntity.ok(updatedOrderDetail);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi cập nhật chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable Integer orderDetailId) {
        try {
            // Kiểm tra chi tiết đơn hàng
            OrderDetail existingOrderDetail = orderDetailService.findById(orderDetailId);
            if (existingOrderDetail == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy chi tiết đơn hàng với ID: " + orderDetailId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Lưu đơn hàng trước khi xóa chi tiết
            Order order = existingOrderDetail.getOrder();
            
            // Xóa chi tiết đơn hàng
            orderDetailService.deleteById(orderDetailId);
            
            // Cập nhật tổng tiền đơn hàng
            updateOrderTotalAmount(order);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa chi tiết đơn hàng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi xóa chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Phương thức hỗ trợ để cập nhật tổng tiền đơn hàng
    private void updateOrderTotalAmount(Order order) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getOrderId());
        
        Long totalAmount = 0L;
        for (OrderDetail detail : orderDetails) {
            totalAmount += detail.getPrice() * detail.getQuantity();
        }
        
        order.setTotalAmount(totalAmount);
        orderService.save(order);
    }
} 