package com.rasool.example.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rasool.example.entity.Product;
import com.rasool.example.repository.ProductRepository;

/**
 * Service class for handling product operations.
 * @author Rasool Malik Vempalli
 */
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void deleteAllProducts() {
        repository.deleteAll();
    }

//    public Product saveProduct(Product product) {
//        return repository.save(product);
//    }
    
    public Product saveProduct(Product product) {
        // Check if the product with the same name already exists
        List<Product> existingProducts = repository.findByName(product.getName().trim());
        if (!existingProducts.isEmpty()) {
            return existingProducts.get(0);  // Return the existing product if found
        } else {
            return repository.save(product);  // Save the new product if not found
        }
    }
   


    public List<Product> saveProducts(List<Product> products) {
        return repository.saveAll(products);
    }

    public List<Product> getProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return repository.findById(id);
    }

    public List<Product> getProductByName(String name) {
        return repository.findByName(name.trim());
    }

    @Transactional
    public boolean deleteProduct(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteProductWithName(String name) {
        List<Product> products = repository.findByName(name.trim());
        if (!products.isEmpty()) {
            repository.deleteAll(products);
            return true;
        }
        return false;
    }

    @Transactional
    public Product updateProduct(int productId, Product product) {
        return repository.findById(productId).map(existingProduct -> {
            existingProduct.setName(product.getName());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setPrice(product.getPrice());
            return repository.save(existingProduct);
        }).orElse(null);
    }
}
