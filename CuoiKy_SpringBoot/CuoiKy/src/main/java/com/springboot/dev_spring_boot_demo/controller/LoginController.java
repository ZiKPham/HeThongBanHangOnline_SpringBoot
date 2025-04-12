package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Order;
import com.springboot.dev_spring_boot_demo.entity.Product;
import com.springboot.dev_spring_boot_demo.entity.User;
import com.springboot.dev_spring_boot_demo.service.OrderService;
import com.springboot.dev_spring_boot_demo.service.ProductService;
import com.springboot.dev_spring_boot_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.DataSource;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String root(Model model) {
        // Lấy 4 sản phẩm mới nhất để hiển thị ở trang chủ
        List<Product> allProducts = productService.findAll();
        List<Product> featuredProducts = allProducts.stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .limit(4)
                .toList();
        model.addAttribute("featuredProducts", featuredProducts);
        return "redirect:/shop";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "checkout", required = false) String checkout, Model model) {
        if (checkout != null) {
            model.addAttribute("checkout", true);
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("passwordError", "Mật khẩu và xác nhận mật khẩu không khớp");
            return "register";
        }
        
        try {
            // Gọi service để xử lý đăng ký
            boolean success = userService.registerUser(user);
            if (!success) {
                model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại");
                return "register";
            }
            
            // Đăng ký thành công
            redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công!");
            return "redirect:/login";
            
        } catch (Exception e) {
            // Xử lý lỗi
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    
    @GetMapping("/admin")
    public String admin(Model model) {
        try {
            // Lấy danh sách đơn hàng cần xử lý thống kê
            List<Order> allOrders = orderService.findAll();
            
            // Đếm số đơn hàng theo trạng thái
            long pendingOrders = allOrders.stream()
                    .filter(order -> order.getStatus().equals(Order.OrderStatus.PENDING.name()))
                    .count();
            
            long completedOrders = allOrders.stream()
                    .filter(order -> order.getStatus().equals(Order.OrderStatus.COMPLETED.name()))
                    .count();
            
            long cancelledOrders = allOrders.stream()
                    .filter(order -> order.getStatus().equals(Order.OrderStatus.CANCELLED.name()))
                    .count();
            
            // Tính tổng doanh thu
            long totalRevenue = allOrders.stream()
                    .filter(order -> order.getStatus().equals(Order.OrderStatus.COMPLETED.name()))
                    .mapToLong(Order::getTotalAmount)
                    .sum();
            
            // Tỷ lệ đơn hàng hoàn thành
            int completionRate = allOrders.isEmpty() ? 0 : 
                    (int) (completedOrders * 100 / allOrders.size());
                    
            // Truyền dữ liệu vào model
            model.addAttribute("pendingOrders", pendingOrders);
            model.addAttribute("completedOrders", completedOrders);
            model.addAttribute("cancelledOrders", cancelledOrders);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("completionRate", completionRate);
            
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có
            System.err.println("Lỗi khi lấy dữ liệu đơn hàng: " + e.getMessage());
        }
        
        return "admin/admin"; 
    }
    
    @GetMapping("/shop")
    public String shop(Model model) {
        // Lấy 4 sản phẩm mới nhất để hiển thị ở trang chủ
        List<Product> allProducts = productService.findAll();
        List<Product> featuredProducts = allProducts.stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .limit(4)
                .toList();
        model.addAttribute("featuredProducts", featuredProducts);
        return "index";
    }
    
    // Trang dashboard sau khi đăng nhập
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/shop";
    }

    
}
