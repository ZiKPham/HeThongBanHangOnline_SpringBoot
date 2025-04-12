package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.User;
import com.springboot.dev_spring_boot_demo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminUserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "sort", required = false, defaultValue = "username_asc") String sort,
            Model model) {
        
        // Lấy danh sách người dùng
        List<User> allUsers = userService.findAll();
        
        // Lọc người dùng theo từ khóa tìm kiếm (nếu có)
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> search == null || search.isEmpty() ||
                        user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                        user.getFullname().toLowerCase().contains(search.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        
        // Sắp xếp danh sách người dùng
        switch (sort) {
            case "username_desc":
                filteredUsers.sort((u1, u2) -> u2.getUsername().compareTo(u1.getUsername()));
                break;
            case "fullname_asc":
                filteredUsers.sort(Comparator.comparing(User::getFullname));
                break;
            case "fullname_desc":
                filteredUsers.sort((u1, u2) -> u2.getFullname().compareTo(u1.getFullname()));
                break;
            case "email_asc":
                filteredUsers.sort(Comparator.comparing(User::getEmail));
                break;
            case "email_desc":
                filteredUsers.sort((u1, u2) -> u2.getEmail().compareTo(u1.getEmail()));
                break;
            default: // username_asc
                filteredUsers.sort(Comparator.comparing(User::getUsername));
                break;
        }
        
        model.addAttribute("users", filteredUsers);
        model.addAttribute("sort", sort);
        
        return "admin/users/list-users";
    }

    @GetMapping("/{username}/view")
    public String viewUser(@PathVariable("username") String username, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng với tên đăng nhập: " + username);
            return "redirect:/admin/users";
        }
        
        model.addAttribute("user", user);
        return "admin/users/view-user";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Tạo đối tượng User mới
        User user = new User();
        model.addAttribute("user", user);
        
        return "admin/users/form-user";
    }

    @GetMapping("/{username}/edit")
    public String showEditForm(@PathVariable("username") String username, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng với tên đăng nhập: " + username);
            return "redirect:/admin/users";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("isEditing", true);
        
        return "admin/users/form-user";
    }

    @PostMapping("/save")
    public String saveUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(name = "isEditing", required = false) boolean isEditing,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Kiểm tra xem người dùng đã tồn tại chưa (đối với trường hợp thêm mới)
            if (!isEditing) {
                User existingUser = userService.findByUsername(user.getUsername());
                if (existingUser != null) {
                    bindingResult.rejectValue("username", "error.username", "Tên đăng nhập đã tồn tại");
                }
                
                // Kiểm tra mật khẩu cho người dùng mới
                if (user.getNewPassword() == null || user.getNewPassword().isEmpty()) {
                    bindingResult.rejectValue("newPassword", "error.password", "Vui lòng nhập mật khẩu cho người dùng mới");
                }
            }

            // Kiểm tra xác nhận mật khẩu
            if (user.getNewPassword() != null && !user.getNewPassword().isEmpty()) {
                if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
                    bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Vui lòng xác nhận mật khẩu");
                } else if (!user.getNewPassword().equals(user.getConfirmPassword())) {
                    bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
                }
            }

            // Nếu có lỗi validation, trả về form
            if (bindingResult.hasErrors()) {
                model.addAttribute("isEditing", isEditing);
                return "admin/users/form-user";
            }

            // Xử lý mật khẩu
            if (!isEditing) {
                // Mã hóa mật khẩu cho người dùng mới
                String encodedPassword = passwordEncoder.encode(user.getNewPassword());
                if (!encodedPassword.startsWith("{bcrypt}")) {
                    encodedPassword = "{bcrypt}" + encodedPassword;
                }
                user.setPassword(encodedPassword);
            } else {
                // Trường hợp cập nhật
                User existingUser = userService.findByUsername(user.getUsername());
                
                // Giữ nguyên mật khẩu cũ nếu không có mật khẩu mới
                if (user.getNewPassword() == null || user.getNewPassword().isEmpty()) {
                    user.setPassword(existingUser.getPassword());
                } else {
                    // Cập nhật mật khẩu mới
                    String encodedPassword = passwordEncoder.encode(user.getNewPassword());
                    if (!encodedPassword.startsWith("{bcrypt}")) {
                        encodedPassword = "{bcrypt}" + encodedPassword;
                    }
                    user.setPassword(encodedPassword);
                }
            }
            
            // Lưu thông tin người dùng
            userService.save(user);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success", 
                    (isEditing ? "Cập nhật" : "Thêm mới") + " người dùng thành công");
            
            return "redirect:/admin/users";
            
        } catch (Exception e) {
            // Xử lý lỗi
            bindingResult.reject("error.global", "Lỗi khi lưu thông tin người dùng: " + e.getMessage());
            model.addAttribute("isEditing", isEditing);
            return "admin/users/form-user";
        }
    }

    @DeleteMapping("/{username}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username) {
        try {
            // Kiểm tra người dùng có tồn tại không
            User user = userService.findByUsername(username);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Người dùng không tồn tại");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Không cho phép xóa người dùng đang đăng nhập
            // Điều này nên được kiểm tra với SecurityContext, nhưng tạm thời bỏ qua
            
            // Xóa người dùng
            userService.deleteByUsername(username);
            
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