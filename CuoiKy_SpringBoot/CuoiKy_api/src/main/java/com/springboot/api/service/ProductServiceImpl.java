package com.springboot.api.service;

import com.springboot.api.entity.Product;
import com.springboot.api.entity.Product.Category;
import com.springboot.api.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    @Autowired
    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productDao.findByCategory(category);
    }

    @Override
    public List<Product> searchProductsByName(String keyword) {
        return productDao.findByNameContaining(keyword);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productDao.findByBrand(brand);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productDao.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productDao.deleteById(id);
    }
} 