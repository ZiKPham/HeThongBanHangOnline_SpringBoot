package com.springboot.api.dao;

import com.springboot.api.entity.Product;
import com.springboot.api.entity.Product.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> query = entityManager.createQuery("from Product", Product.class);
        return query.getResultList();
    }

    @Override
    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        TypedQuery<Product> query = entityManager.createQuery(
            "from Product where category = :category", Product.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        TypedQuery<Product> query = entityManager.createQuery(
            "from Product where lower(name) like :name", Product.class);
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    @Override
    public List<Product> findByBrand(String brand) {
        TypedQuery<Product> query = entityManager.createQuery(
            "from Product where lower(brand) = :brand", Product.class);
        query.setParameter("brand", brand.toLowerCase());
        return query.getResultList();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    public void deleteById(Long id) {
        Product product = findById(id);
        if (product != null) {
            entityManager.remove(product);
        }
    }
} 