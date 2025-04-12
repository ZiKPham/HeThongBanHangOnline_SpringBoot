package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.User;
import com.springboot.dev_spring_boot_demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.logging.Logger;

@Controller
@RequestMapping("/account")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String viewAccount(Authentication authentication, Model model) {
        logger.info("Đang truy cập trang quản lý tài khoản");
        
        try {
            // Lấy thông tin người dùng hiện tại
            String username = authentication.getName();
            logger.info("Username đang đăng nhập: " + username);
            
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warning("Không tìm thấy thông tin người dùng: " + username);
                return "redirect:/login";
            }
            
            logger.info("Đã lấy thông tin người dùng thành công: " + user.getUsername());
            model.addAttribute("user", user);
            return "account/profile";
        } catch (Exception e) {
            logger.severe("Lỗi khi truy cập trang quản lý tài khoản: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/edit-profile")
    public String showEditProfileForm(Authentication authentication, Model model) {
        // Lấy thông tin người dùng hiện tại
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        model.addAttribute("user", user);
        return "account/edit-profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Lấy thông tin người dùng hiện tại
        String username = authentication.getName();
        
        // Kiểm tra xem người dùng có đang cập nhật thông tin của chính mình không
        if (!username.equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền cập nhật thông tin người dùng khác");
            return "redirect:/account";
        }
        
        // Nếu có lỗi validation, trả về form
        if (bindingResult.hasErrors()) {
            return "account/edit-profile";
        }
        
        try {
            // Lấy thông tin người dùng hiện tại từ DB
            User currentUser = userService.findByUsername(username);
            
            // Cập nhật thông tin (chỉ thông tin được phép cập nhật)
            currentUser.setFullname(user.getFullname());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            currentUser.setAddress(user.getAddress());
            
            // Lưu thông tin người dùng
            userService.save(currentUser);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin cá nhân thành công");
            
            return "redirect:/account";
            
        } catch (Exception e) {
            // Xử lý lỗi
            model.addAttribute("error", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            return "account/edit-profile";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "account/change-password";
    }

    @PostMapping("/update-password")
    public String updatePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Lấy thông tin người dùng hiện tại
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        // Kiểm tra mật khẩu hiện tại
        String currentEncodedPassword = user.getPassword();
        if (currentEncodedPassword.startsWith("{bcrypt}")) {
            currentEncodedPassword = currentEncodedPassword.substring(8);
        }
        
        if (!passwordEncoder.matches(currentPassword, currentEncodedPassword)) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng");
            return "account/change-password";
        }
        
        // Kiểm tra mật khẩu mới
        if (newPassword == null || newPassword.isEmpty()) {
            model.addAttribute("error", "Mật khẩu mới không được để trống");
            return "account/change-password";
        }
        
        if (newPassword.length() < 6) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
            return "account/change-password";
        }
        
        // Kiểm tra xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            return "account/change-password";
        }
        
        try {
            // Mã hóa mật khẩu mới
            String encodedPassword = passwordEncoder.encode(newPassword);
            if (!encodedPassword.startsWith("{bcrypt}")) {
                encodedPassword = "{bcrypt}" + encodedPassword;
            }
            
            // Cập nhật mật khẩu
            user.setPassword(encodedPassword);
            userService.save(user);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
            
            return "redirect:/account";
            
        } catch (Exception e) {
            // Xử lý lỗi
            model.addAttribute("error", "Lỗi khi đổi mật khẩu: " + e.getMessage());
            return "account/change-password";
        }
    }
}
