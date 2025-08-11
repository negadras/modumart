package com.negadras.modumart.catalog;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public ProductService(ProductRepository productRepository, ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public List<Product> getAllProducts() {
        return (List<Product>) productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        eventPublisher.publishEvent(new ProductCreatedEvent(savedProduct.id()));
        return savedProduct;
    }
    
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    Product updated = new Product(id, updatedProduct.name(), updatedProduct.description(), 
                            updatedProduct.price(), updatedProduct.stock(), updatedProduct.category());
                    return productRepository.save(updated);
                });
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getProductsInStock() {
        return productRepository.findProductsInStock(0);
    }
    
    public boolean reduceStock(Long productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (product.stock() >= quantity) {
                        Product updated = product.withStock(product.stock() - quantity);
                        productRepository.save(updated);
                        eventPublisher.publishEvent(new ProductStockReducedEvent(productId, quantity));
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}