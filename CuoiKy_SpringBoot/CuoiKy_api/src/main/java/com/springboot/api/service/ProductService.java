package com.springboot.api.service;

import com.springboot.api.entity.Product;
import com.springboot.api.entity.Product.Category;
import java.util.List;

public interface ProductService {
    
    List<Product> getAllProducts();
    
    Product getProductById(Long id);
    
    List<Product> getProductsByCategory(Category category);
    
    List<Product> searchProductsByName(String keyword);
    
    List<Product> getProductsByBrand(String brand);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
} 