package com.rasool.example;

import com.rasool.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestH2Repository extends JpaRepository<Product,Integer> {

}