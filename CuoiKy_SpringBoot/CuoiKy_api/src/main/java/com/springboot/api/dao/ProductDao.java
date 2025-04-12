package com.springboot.api.dao;

import com.springboot.api.entity.Product;
import com.springboot.api.entity.Product.Category;
import java.util.List;

public interface ProductDao {
    
    List<Product> findAll();
    
    Product findById(Long id);
    
    List<Product> findByCategory(Category category);
    
    List<Product> findByNameContaining(String name);
    
    List<Product> findByBrand(String brand);
    
    Product save(Product product);
    
    void deleteById(Long id);
} 