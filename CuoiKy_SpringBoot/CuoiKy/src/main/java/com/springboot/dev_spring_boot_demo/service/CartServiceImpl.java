package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.dao.CartDAO;
import com.springboot.dev_spring_boot_demo.dao.ProductDAO;
import com.springboot.dev_spring_boot_demo.entity.Cart;
import com.springboot.dev_spring_boot_demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    @Autowired
    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public List<Cart> findByUsername(String username) {
        return cartDAO.findByUsername(username);
    }

    @Override
    @Transactional
    public Cart addToCart(Integer productId, Integer quantity) {
        // Chuyển đổi Integer sang Long
        Product product = productDAO.findById(productId.longValue());
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Đảm bảo sản phẩm vẫn còn đủ số lượng trong kho
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Không đủ số lượng sản phẩm trong kho");
        }

        // Lấy username của người dùng hiện tại
        String username = "guest";
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                username = authentication.getName();
            }
        } catch (Exception e) {
            // Nếu có lỗi khi lấy thông tin người dùng, sử dụng "guest"
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng của người dùng này chưa
        List<Cart> existingItems = cartDAO.findByUsernameAndProductId(username, productId);
        if (!existingItems.isEmpty()) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            Cart existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartDAO.save(existingItem);
        }
        
        // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới
        Cart cart = new Cart();
        cart.setUsername(username);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setPrice(product.getPrice());
        cart.setAddedAt(LocalDateTime.now());

        return cartDAO.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(Integer cartId) {
        cartDAO.deleteById(cartId);
    }

    @Override
    @Transactional
    public void clearCart(String username) {
        cartDAO.deleteByUsername(username);
    }

    @Override
    public Long getCartTotal(String username) {
        List<Cart> cartItems = cartDAO.findByUsername(username);
        return cartItems.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public List<Cart> getCartItems() {
        // Lấy giỏ hàng từ session hoặc cookie
        return cartDAO.findAll();
    }

    @Override
    @Transactional
    public Cart addToCartWithoutLogin(Integer productId, Integer quantity) {
        Product product = productDAO.findById(productId.longValue());
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        // Đảm bảo sản phẩm vẫn còn đủ số lượng trong kho
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Không đủ số lượng sản phẩm trong kho");
        }

        // Lấy username hiện tại nếu đã đăng nhập, nếu không thì sử dụng "guest"
        String username = "guest";
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                username = authentication.getName();
            }
        } catch (Exception e) {
            // Nếu có lỗi khi lấy thông tin người dùng, sử dụng "guest"
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng của người dùng chưa
        List<Cart> existingItems = cartDAO.findByUsernameAndProductId(username, productId);
        if (!existingItems.isEmpty()) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            Cart existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartDAO.save(existingItem);
        }

        // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới
        Cart cart = new Cart();
        cart.setUsername(username); 
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setPrice(product.getPrice());
        cart.setAddedAt(LocalDateTime.now());

        return cartDAO.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Integer cartId, Integer quantity) {
        // Kiểm tra cartId hợp lệ
        Cart cart = cartDAO.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Kiểm tra số lượng tồn kho của sản phẩm
        Product product = cart.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Không đủ số lượng sản phẩm trong kho");
        }

        // Cập nhật số lượng
        cart.setQuantity(quantity);
        
        return cartDAO.save(cart);
    }

    @Override
    @Transactional
    public void transferGuestCartToUser(String username) {
        // Lấy danh sách giỏ hàng của khách
        List<Cart> guestCartItems = cartDAO.findByUsername("guest");
        
        if (guestCartItems.isEmpty()) {
            return; // Không có sản phẩm trong giỏ hàng khách
        }
        
        // Lấy danh sách giỏ hàng hiện tại của người dùng
        List<Cart> userCartItems = cartDAO.findByUsername(username);
        
        // Duyệt qua từng sản phẩm trong giỏ hàng khách
        for (Cart guestItem : guestCartItems) {
            boolean productExistsInUserCart = false;
            
            // Kiểm tra xem sản phẩm đã có trong giỏ hàng người dùng chưa
            for (Cart userItem : userCartItems) {
                if (userItem.getProduct().getId().equals(guestItem.getProduct().getId())) {
                    // Nếu đã có, cập nhật số lượng
                    userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
                    cartDAO.save(userItem);
                    productExistsInUserCart = true;
                    break;
                }
            }
            
            // Nếu sản phẩm chưa có trong giỏ hàng người dùng
            if (!productExistsInUserCart) {
                // Tạo bản sao của mục giỏ hàng khách với username của người dùng
                Cart newUserCartItem = new Cart();
                newUserCartItem.setUsername(username);
                newUserCartItem.setProduct(guestItem.getProduct());
                newUserCartItem.setQuantity(guestItem.getQuantity());
                newUserCartItem.setPrice(guestItem.getPrice());
                newUserCartItem.setAddedAt(LocalDateTime.now());
                cartDAO.save(newUserCartItem);
            }
        }
        
        // Xóa giỏ hàng khách sau khi đã chuyển hết
        clearCart("guest");
    }
} 