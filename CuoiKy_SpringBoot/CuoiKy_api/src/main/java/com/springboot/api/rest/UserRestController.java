package com.springboot.api.rest;

import com.springboot.api.entity.User;
import com.springboot.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/users/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/users/phone/{phone}")
    public ResponseEntity<User> getUserByPhone(@PathVariable String phone) {
        User user = userService.getUserByPhone(phone);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Kiểm tra tài khoản đã tồn tại chưa
        Map<String, String> errors = new HashMap<>();
        
        if (userService.existsByUsername(user.getUsername())) {
            errors.put("username", "Tên đăng nhập đã được sử dụng");
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            errors.put("email", "Email đã được sử dụng");
        }
        
        if (user.getPhone() != null && userService.existsByPhone(user.getPhone())) {
            errors.put("phone", "Số điện thoại đã được sử dụng");
        }
        
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Lưu người dùng mới
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User user) {
        User existingUser = userService.getUserByUsername(username);
        
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Kiểm tra xem username/email/phone đã được sử dụng chưa (trừ user hiện tại)
        Map<String, String> errors = new HashMap<>();
        
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userService.existsByUsername(user.getUsername())) {
            errors.put("username", "Tên đăng nhập đã được sử dụng");
        }
        
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userService.existsByEmail(user.getEmail())) {
            errors.put("email", "Email đã được sử dụng");
        }
        
        if (user.getPhone() != null && 
            (existingUser.getPhone() == null || !existingUser.getPhone().equals(user.getPhone())) && 
            userService.existsByPhone(user.getPhone())) {
            errors.put("phone", "Số điện thoại đã được sử dụng");
        }
        
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        
        // Nếu password mới được gửi lên, mã hóa nó
        if (user.getPassword() != null && !user.getPassword().isEmpty() && 
            !user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Giữ nguyên password cũ
            user.setPassword(existingUser.getPassword());
        }
        
        User updatedUser = userService.updateUser(username, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        User existingUser = userService.getUserByUsername(username);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        userService.deleteUser(username);
        return ResponseEntity.ok("Xóa người dùng thành công với username = " + username);
    }
} 