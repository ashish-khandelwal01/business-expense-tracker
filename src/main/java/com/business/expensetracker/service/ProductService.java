package com.business.expensetracker.service;

import com.business.expensetracker.entity.Product;
import com.business.expensetracker.entity.User;
import com.business.expensetracker.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CurrentUserService currentUserService;

    public List<Product> getAllProducts() {
        return productRepository.findAllByOwner(currentUserService.getCurrentUser());
    }

    @Cacheable("products")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findByIdAndOwner(id, currentUserService.getCurrentUser());
    }

    public Optional<Product> getProductByName(String productName) {
        return productRepository.findByProductNameAndOwner(productName, currentUserService.getCurrentUser());
    }

    public Product createProduct(Product product) {
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        product.setOwner(currentUserService.getCurrentUser());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findByIdAndOwner(id, currentUserService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        if (productDetails.getStockQuantity() != null) {
            product.setStockQuantity(productDetails.getStockQuantity());
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndOwner(id, currentUserService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    public Product updateStock(Long productId, Integer quantityChange) {
        Product product = productRepository.findByIdAndOwner(productId, currentUserService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        int newStock = (product.getStockQuantity() + quantityChange);
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }
}
