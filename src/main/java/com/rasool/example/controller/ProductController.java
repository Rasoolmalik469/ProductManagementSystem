package com.rasool.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.rasool.example.entity.Product;
import com.rasool.example.service.ProductService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller for product-related operations.
 * @author Rasool Malik Vempalli
 */
@RestController
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        Product savedProduct = service.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping("/addProducts")
    public ResponseEntity<List<Product>> addProducts(@Valid @RequestBody List<Product> products) {
        List<Product> savedProducts = service.saveProducts(products);
        return new ResponseEntity<>(savedProducts, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> findAllProducts() {
        List<Product> products = service.getProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/productById/{id}")
    public ResponseEntity<Optional<Product>> findProductById(@PathVariable int id) {
        Optional<Product> product = service.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/product/{name}")
    public ResponseEntity<List<Product>> findProductByName(@PathVariable String name) {
        List<Product> products = service.getProductByName(name);
        if (!products.isEmpty()) {
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product, @PathVariable int id) {
        Product updatedProduct = service.updateProduct(id, product);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        if (service.deleteProduct(id)) {
            return ResponseEntity.ok("Product removed !! " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletebyName/{name}")
    public ResponseEntity<String> deleteProductWithName(@PathVariable String name) {
        if (service.deleteProductWithName(name)) {
            return ResponseEntity.ok("Product named '" + name + "' has been removed.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteAllProducts")
    public ResponseEntity<Void> deleteAllProducts() {
        service.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }
}

