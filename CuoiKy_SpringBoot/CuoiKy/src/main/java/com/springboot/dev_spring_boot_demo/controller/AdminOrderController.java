package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Order;
import com.springboot.dev_spring_boot_demo.entity.OrderDetail;
import com.springboot.dev_spring_boot_demo.entity.User;
import com.springboot.dev_spring_boot_demo.service.OrderService;
import com.springboot.dev_spring_boot_demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public AdminOrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String listOrders(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false, defaultValue = "date_desc") String sort,
            Model model) {
        
        // Lấy tất cả đơn hàng
        List<Order> allOrders = orderService.findAll();
        
        // Lọc theo từ khóa nếu có
        List<Order> filteredOrders = allOrders;
        if (search != null && !search.isEmpty()) {
            filteredOrders = allOrders.stream()
                    .filter(order -> String.valueOf(order.getOrderId()).contains(search) ||
                            order.getUsername().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        // Lọc theo trạng thái nếu có
        if (status != null && !status.isEmpty()) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> order.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        
        // Sắp xếp đơn hàng
        switch (sort) {
            case "id_asc":
                filteredOrders.sort(Comparator.comparing(Order::getOrderId));
                break;
            case "id_desc":
                filteredOrders.sort((o1, o2) -> o2.getOrderId().compareTo(o1.getOrderId()));
                break;
            case "date_asc":
                filteredOrders.sort(Comparator.comparing(Order::getOrderDate));
                break;
            case "date_desc":
                filteredOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                break;
            case "total_asc":
                filteredOrders.sort(Comparator.comparing(Order::getTotalAmount));
                break;
            case "total_desc":
                filteredOrders.sort((o1, o2) -> o2.getTotalAmount().compareTo(o1.getTotalAmount()));
                break;
            default:
                // Mặc định sắp xếp theo ngày tạo giảm dần (mới nhất lên đầu)
                filteredOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                break;
        }
        
        // Format đặc biệt cho datetime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Truyền dữ liệu vào Model
        model.addAttribute("orders", filteredOrders);
        model.addAttribute("formatter", formatter);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("statusOptions", Arrays.asList("PENDING", "COMPLETED", "CANCELLED"));
        
        return "admin/orders/list-orders";
    }
    
    @GetMapping("/{orderId}/view")
    public String viewOrder(@PathVariable("orderId") Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng với ID: " + orderId);
                return "redirect:/admin/orders";
            }
            
            // Lấy thông tin chi tiết đơn hàng
            List<OrderDetail> orderDetails = order.getOrderDetails();
            if (orderDetails == null) {
                orderDetails = new ArrayList<>();
            }
            
            // Lấy thông tin người dùng đặt hàng
            User user = userService.findByUsername(order.getUsername());
            
            // Format đặc biệt cho datetime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", orderDetails);
            model.addAttribute("user", user);
            model.addAttribute("formatter", formatter);
            model.addAttribute("statusOptions", Arrays.asList("PENDING", "COMPLETED", "CANCELLED"));
            
            return "admin/orders/view-order";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xem chi tiết đơn hàng: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }
    
    @PostMapping("/{orderId}/update-status")
    public String updateOrderStatus(
            @PathVariable("orderId") Integer orderId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công");
            return "redirect:/admin/orders/" + orderId + "/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            return "redirect:/admin/orders/" + orderId + "/view";
        }
    }
    
    @DeleteMapping("/{orderId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteOrder(@PathVariable("orderId") Integer orderId) {
        try {
            // Kiểm tra đơn hàng có tồn tại không
            Order order = orderService.findById(orderId);
            if (order == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Xóa đơn hàng
            orderService.deleteById(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 