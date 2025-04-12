package com.springboot.dev_spring_boot_demo.service;

import com.springboot.dev_spring_boot_demo.entity.Cart;
import java.util.List;

public interface CartService {
    List<Cart> findByUsername(String username);
    Cart addToCart(Integer productId, Integer quantity);
    void removeFromCart(Integer cartId);
    void clearCart(String username);
    Long getCartTotal(String username);
    List<Cart> getCartItems();
    Cart addToCartWithoutLogin(Integer productId, Integer quantity);
    Cart updateCartItemQuantity(Integer cartId, Integer quantity);
    void transferGuestCartToUser(String username);
} 