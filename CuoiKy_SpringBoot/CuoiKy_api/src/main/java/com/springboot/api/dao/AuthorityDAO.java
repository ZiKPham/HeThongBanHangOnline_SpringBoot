package com.springboot.api.dao;

import com.springboot.api.entity.Authority;
import java.util.List;

public interface AuthorityDAO {
    List<Authority> findAll();
    List<Authority> findByUsername(String username);
    Authority findById(String username);
    Authority save(Authority authority);
    void deleteById(String username);
} 