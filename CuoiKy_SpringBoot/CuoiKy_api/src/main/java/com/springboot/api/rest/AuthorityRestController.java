package com.springboot.api.rest;

import com.springboot.api.entity.Authority;
import com.springboot.api.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityRestController {

    private AuthorityService authorityService;

    @Autowired
    public AuthorityRestController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthorities() {
        try {
            List<Authority> authorities = authorityService.findAll();
            return ResponseEntity.ok(authorities);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi lấy danh sách quyền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getAuthoritiesByUsername(@PathVariable String username) {
        try {
            List<Authority> authorities = authorityService.findByUsername(username);
            if (authorities.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy quyền cho người dùng có username: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(authorities);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi lấy danh sách quyền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAuthority(@PathVariable String username) {
        try {
            Authority authority = authorityService.findById(username);
            if (authority == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy quyền với username: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(authority);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi lấy thông tin quyền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createAuthority(@RequestBody Authority authority) {
        try {
            // Kiểm tra xem quyền đã tồn tại chưa
            Authority existingAuthority = authorityService.findById(authority.getUsername());
            if (existingAuthority != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Quyền đã tồn tại với username: " + authority.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            Authority savedAuthority = authorityService.save(authority);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthority);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi tạo quyền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateAuthority(@PathVariable String username, @RequestBody Authority authority) {
        try {
            Authority existingAuthority = authorityService.findById(username);
            if (existingAuthority == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy quyền với username: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            authority.setUsername(username); // Đảm bảo username không thay đổi
            Authority updatedAuthority = authorityService.save(authority);
            return ResponseEntity.ok(updatedAuthority);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi cập nhật quyền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteAuthority(@PathVariable String username) {
        try {
            Authority existingAuthority = authorityService.findById(username);
            if (existingAuthority == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Không tìm thấy quyền với username: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            authorityService.deleteById(username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa quyền thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Lỗi khi xóa quyền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}