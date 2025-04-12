package com.springboot.dev_spring_boot_demo.controller;

import com.springboot.dev_spring_boot_demo.entity.Order;
import com.springboot.dev_spring_boot_demo.entity.Product;
import com.springboot.dev_spring_boot_demo.entity.Cart;
import com.springboot.dev_spring_boot_demo.service.CartService;
import com.springboot.dev_spring_boot_demo.service.OrderService;
import com.springboot.dev_spring_boot_demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    @Autowired
    public ShopController(ProductService productService, CartService cartService, OrderService orderService) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    // Các chức năng của ProductController
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "sort", required = false, defaultValue = "price_desc") String sort,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "ram", required = false) String ram,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model) {
        
        // Lấy tất cả sản phẩm
        List<Product> allProducts = productService.findAll();
        
        // Lọc sản phẩm theo các tiêu chí
        List<Product> filteredProducts = allProducts.stream()
                // Lọc theo thương hiệu
                .filter(p -> brand == null || brand.isEmpty() || p.getBrand().equals(brand))
                // Lọc theo loại laptop
                .filter(p -> category == null || category.isEmpty() || 
                        p.getCategory().name().equals(category))
                // Lọc theo RAM
                .filter(p -> ram == null || ram.isEmpty() || p.getRam().equals(ram))
                // Lọc theo giá tối thiểu
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                // Lọc theo giá tối đa
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .toList();
        
        // Sắp xếp sản phẩm theo tiêu chí
        List<Product> sortedProducts = switch (sort) {
            case "price_asc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                    .toList();
            case "price_desc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                    .toList();
            case "name_asc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                    .toList();
            case "name_desc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p2.getName().compareTo(p1.getName()))
                    .toList();
            default -> filteredProducts.stream()
                    .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                    .toList();
        };
        
        // Phân trang
        int pageSize = 8; // Số sản phẩm trên mỗi trang
        int start = Math.min(page * pageSize, sortedProducts.size());
        int end = Math.min(start + pageSize, sortedProducts.size());
        List<Product> pagedProducts = sortedProducts.subList(start, end);
        
        // Tính toán thông tin phân trang
        int totalPages = (int) Math.ceil((double) sortedProducts.size() / pageSize);
        
        // Thêm các thuộc tính vào model
        model.addAttribute("products", pagedProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        // Lấy danh sách thương hiệu để làm filter
        List<String> brands = allProducts.stream()
                .map(Product::getBrand)
                .distinct()
                .sorted()
                .toList();
        model.addAttribute("brands", brands);
        
        return "default/list-products";
    }

    @GetMapping("/products/gaming")
    public String listGamingLaptops(Model model) {
        return "redirect:/shop/products?category=LAPTOP_GAMING";
    }

    @GetMapping("/products/office")
    public String listOfficeLaptops(Model model) {
        return "redirect:/shop/products?category=LAPTOP_OFFICE";
    }

    @GetMapping("/products/detail/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/shop/products?error=product-not-found";
        }
        model.addAttribute("product", product);
        return "default/product-detail";
    }

    @GetMapping("/products/search")
    public String searchProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "sort", required = false, defaultValue = "price_desc") String sort,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "ram", required = false) String ram,
            Model model) {
        
        // Lấy tất cả sản phẩm
        List<Product> allProducts = productService.findAll();
        
        // Lọc sản phẩm theo từ khóa tìm kiếm
        List<Product> filteredProducts = allProducts.stream()
                // Lọc theo từ khóa tìm kiếm (nếu có)
                .filter(p -> keyword == null || keyword.isEmpty() || 
                        p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getBrand().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getCpu().toLowerCase().contains(keyword.toLowerCase()))
                // Lọc theo thương hiệu
                .filter(p -> brand == null || brand.isEmpty() || p.getBrand().equals(brand))
                // Lọc theo loại laptop
                .filter(p -> category == null || category.isEmpty() || 
                        p.getCategory().name().equals(category))
                // Lọc theo RAM
                .filter(p -> ram == null || ram.isEmpty() || p.getRam().equals(ram))
                // Lọc theo giá tối thiểu
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                // Lọc theo giá tối đa
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .toList();
        
        // Sắp xếp sản phẩm theo tiêu chí
        List<Product> sortedProducts = switch (sort) {
            case "price_asc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                    .toList();
            case "price_desc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                    .toList();
            case "name_asc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                    .toList();
            case "name_desc" -> filteredProducts.stream()
                    .sorted((p1, p2) -> p2.getName().compareTo(p1.getName()))
                    .toList();
            default -> filteredProducts.stream()
                    .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                    .toList();
        };
        
        model.addAttribute("products", sortedProducts);
        model.addAttribute("keyword", keyword);

        // Lấy danh sách thương hiệu để làm filter
        List<String> brands = allProducts.stream()
                .map(Product::getBrand)
                .distinct()
                .sorted()
                .toList();
        model.addAttribute("brands", brands);

        return "default/list-products";
    }

    // Các chức năng của CartController
    @GetMapping("/cart")
    public String viewCart(Model model) {
        try {
            // Lấy username hiện tại nếu đã đăng nhập, nếu không thì sử dụng "guest"
            String username = "guest";
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                username = authentication.getName();
            }
            
            // Lấy giỏ hàng dựa trên username
            List<Cart> cartItems = cartService.findByUsername(username);
            Long totalAmount = cartItems.stream()
                    .mapToLong(item -> item.getQuantity() * item.getPrice())
                    .sum();

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);

        } catch (Exception e) {
            // Nếu có lỗi, tạo giỏ hàng trống
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("totalAmount", 0L);
        }

        return "default/view-cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                          @RequestParam("quantity") Integer quantity) {
        try {
            Product product = productService.findById(productId);
            if (product == null) {
                return "redirect:/shop/products?error=product-not-found";
            }
            cartService.addToCartWithoutLogin(productId.intValue(), quantity);
            return "redirect:/shop/cart";
        } catch (Exception e) {
            return "redirect:/shop/products?error=add-to-cart-failed";
        }
    }

    @GetMapping("/cart/remove/{cartId}")
    public String removeFromCart(@PathVariable("cartId") Integer cartId) {
        cartService.removeFromCart(cartId);
        return "redirect:/shop/cart";
    }
    
    @PostMapping("/cart/update")
    @ResponseBody
    public ResponseEntity<?> updateCart(@RequestParam("cartId") Integer cartId,
                                      @RequestParam("quantity") Integer quantity) {
        try {
            // Nếu số lượng nhỏ hơn 1, xóa sản phẩm khỏi giỏ hàng
            if (quantity < 1) {
                cartService.removeFromCart(cartId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("removed", true);
                
                return ResponseEntity.ok(response);
            }
            
            // Nếu số lượng hợp lệ, cập nhật số lượng sản phẩm trong giỏ hàng
            Cart updatedCart = cartService.updateCartItemQuantity(cartId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("updatedQuantity", updatedCart.getQuantity());
            response.put("subtotal", updatedCart.getPrice() * updatedCart.getQuantity());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Các chức năng của OrderController
    @GetMapping("/orders")
    public String listOrders(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("orders", orderService.findByUsername(username));
        } else {
            model.addAttribute("orders", null);
        }
        return "orders/list-orders";
    }

    @GetMapping("/orders/detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Integer orderId, Model model) {
        model.addAttribute("order", orderService.findById(orderId));
        return "orders/order-detail";
    }

    @PostMapping("/orders/save")
    public String saveOrder(@ModelAttribute("order") Order order) {

        orderService.save(order);
        return "redirect:/shop/orders";
    }

    @GetMapping("/orders/cancel/{orderId}")
    public String cancelOrder(@PathVariable("orderId") Integer orderId) {
        orderService.updateOrderStatus(orderId, Order.OrderStatus.CANCELLED.name());
        return "redirect:/shop/orders";
    }

    @GetMapping("/orders/create-from-cart")
    public String createOrderFromCart(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập với thông báo
            return "redirect:/login?checkout=true";
        }
        
        String username = authentication.getName();
        try {
            orderService.createOrderFromCart(username);
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
            return "redirect:/shop/orders";
        } catch (Exception e) {
            // Xử lý trường hợp giỏ hàng trống hoặc lỗi khác
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/shop/cart";
        }
    }

    // Upload image method (admin function)
    @PostMapping("/products/{id}/image")
    public String uploadImage(@PathVariable Long id,
            @RequestParam("image") MultipartFile file) {
        try {
            // Lưu ảnh vào thư mục
            String fileName = "laptop" + id + ".jpg";
            String uploadDir = "src/main/resources/static/product-images/";
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // Cập nhật URL trong database - sửa lại đường dẫn
            Product product = productService.findById(id);
            product.setImageUrl("/product-images/laptop" + id + ".jpg");
            productService.save(product);

            return "redirect:/admin/products";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/products?error=upload";
        }
    }

    @GetMapping("/news-tech")
    public String techNews() {
        return "default/news-tech";
    }

    @GetMapping("/doi-tra")
    public String doiTra() {
        return "default/doi-tra";
    }
    
    @GetMapping("/bao-hanh")
    public String baoHanh() {
        return "default/bao-hanh";
    }
    
    @GetMapping("/thanh-toan")
    public String thanhToan() {
        return "default/thanh-toan";
    }
    
    @GetMapping("/van-chuyen")
    public String vanChuyen() {
        return "default/van-chuyen";
    }

    @GetMapping("/contact")
    public String contact() {
        return "default/contact";
    }

    @GetMapping("/about")
    public String about() {
        return "default/about";
    }
    
} 