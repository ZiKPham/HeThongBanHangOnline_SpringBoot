package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Product;
import com.springboot.dev_spring_boot_demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @Autowired
    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false, defaultValue = "newest") String sort,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model) {

        // Lấy tất cả sản phẩm
        List<Product> allProducts = productService.findAll();
        
        // Lọc sản phẩm theo từ khóa tìm kiếm
        List<Product> filteredProducts = allProducts.stream()
                // Lọc theo từ khóa tìm kiếm (nếu có)
                .filter(p -> search == null || search.isEmpty() || 
                        p.getName().toLowerCase().contains(search.toLowerCase()) ||
                        p.getBrand().toLowerCase().contains(search.toLowerCase()))
                // Lọc theo loại laptop
                .filter(p -> category == null || category.isEmpty() || 
                        p.getCategory().name().equals(category))
                // Lọc theo trạng thái
                .filter(p -> status == null || status.isEmpty() || 
                        ("1".equals(status) && p.getStockQuantity() > 0) ||
                        ("0".equals(status) && p.getStockQuantity() == 0))
                .collect(Collectors.toList());
        
        // Sắp xếp sản phẩm theo tiêu chí
        switch (sort) {
            case "oldest":
                filteredProducts.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
                break;
            case "price_asc":
                filteredProducts.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
                break;
            case "price_desc":
                filteredProducts.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
                break;
            default: // newest
                filteredProducts.sort((p1, p2) -> p2.getId().compareTo(p1.getId()));
                break;
        }

        // Phân trang
        int pageSize = 10;
        int start = Math.min(page * pageSize, filteredProducts.size());
        int end = Math.min(start + pageSize, filteredProducts.size());
        
        List<Product> pagedProducts = filteredProducts.subList(start, end);
        Page<Product> productPage = new PageImpl<>(pagedProducts, PageRequest.of(page, pageSize), filteredProducts.size());
        
        // Thêm danh sách danh mục
        List<Product.Category> categories = Arrays.asList(Product.Category.values());
        
        // Thêm các thuộc tính vào model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sort", sort);
        
        return "admin/products/list-products";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Tạo đối tượng Product mới
        Product product = new Product();
        
        // Thêm danh sách danh mục
        List<Product.Category> categories = Arrays.asList(Product.Category.values());
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        
        return "admin/products/form-product";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Product product = productService.findById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm với ID: " + id);
            return "redirect:/admin/products";
        }
        
        // Thêm danh sách danh mục
        List<Product.Category> categories = Arrays.asList(Product.Category.values());
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        
        return "admin/products/form-product";
    }

    @GetMapping("/{id}/view")
    public String viewProduct(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Product product = productService.findById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm với ID: " + id);
            return "redirect:/admin/products";
        }
        
        model.addAttribute("product", product);
        
        return "admin/products/view-product";
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Kiểm tra lỗi validation
            if (bindingResult.hasErrors()) {
                // Thêm danh sách danh mục
                List<Product.Category> categories = Arrays.asList(Product.Category.values());
                model.addAttribute("categories", categories);
                return "admin/products/form-product";
            }

            // Xác định xem đây là thêm mới hay cập nhật
            boolean isNewProduct = (product.getId() == null);
            
            // Nếu không có URL ảnh và là sản phẩm mới, gán ảnh mặc định
            if ((product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) && isNewProduct) {
                product.setImageUrl("/img/default-product.jpg");
            }
            
            // Lưu sản phẩm vào cơ sở dữ liệu
            productService.save(product);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success", 
                    (isNewProduct ? "Thêm mới" : "Cập nhật") + " sản phẩm thành công");
            
            return "redirect:/admin/products";
            
        } catch (Exception e) {
            // Xử lý lỗi chung
            bindingResult.reject("error.global", "Lỗi khi lưu sản phẩm: " + e.getMessage());
            List<Product.Category> categories = Arrays.asList(Product.Category.values());
            model.addAttribute("categories", categories);
            return "admin/products/form-product";
        }
    }

    @DeleteMapping("/{id}/delete")
    @ResponseBody
    public String deleteProduct(@PathVariable("id") Long id) {
        try {
            // Xóa sản phẩm
            productService.deleteById(id);
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
} 