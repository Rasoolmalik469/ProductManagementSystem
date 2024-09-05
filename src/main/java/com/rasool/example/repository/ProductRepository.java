package com.rasool.example.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.rasool.example.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.name = ?1")
    void deleteByName(String name);
}
