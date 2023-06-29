package com.wadeyuan.store.controller;

import com.wadeyuan.store.domain.Product;
import com.wadeyuan.store.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        product.setCreatedTimestamp(LocalDateTime.now());
        Product createdProduct = productRepository.save(product);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdProduct);
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        if(!productRepository.existsById(productId)) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(productRepository.findById(productId).get());
    }

    @GetMapping
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<Long> deleteProduct(@PathVariable Long productId) {
        if(!productRepository.existsById(productId)) return ResponseEntity.notFound().build();

        productRepository.deleteById(productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        if(!productRepository.existsById(productId)) return ResponseEntity.notFound().build();

        product.setId(productId);
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }
}
