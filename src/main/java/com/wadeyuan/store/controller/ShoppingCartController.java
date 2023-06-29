package com.wadeyuan.store.controller;

import com.wadeyuan.store.domain.Product;
import com.wadeyuan.store.domain.ShoppingCart;
import com.wadeyuan.store.repository.ProductRepository;
import com.wadeyuan.store.repository.ShoppingCartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/shopping-carts")
public class ShoppingCartController {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;

    public ShoppingCartController(ShoppingCartRepository shoppingCartRepository, ProductRepository productRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<ShoppingCart> createShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart createdShoppingCart = shoppingCartRepository.save(shoppingCart);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdShoppingCart.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdShoppingCart);
    }

    @GetMapping(value = "/{shoppingCartId}")
    public ResponseEntity<ShoppingCart> getShoppingCart(@PathVariable Long shoppingCartId) {
        if(!shoppingCartRepository.existsById(shoppingCartId)) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(shoppingCartRepository.findById(shoppingCartId).get());
    }

    @PutMapping(value = "/{shoppingCartId}/add/{productId}")
    public ResponseEntity<ShoppingCart> addToShoppingCart(@PathVariable Long shoppingCartId, @PathVariable Long productId, @RequestParam Integer quantity) {
        if(!shoppingCartRepository.existsById(shoppingCartId)) return ResponseEntity.notFound().build();
        if(!productRepository.existsById(productId)) return ResponseEntity.badRequest().build();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).get();
        Product product = productRepository.findById(productId).get();

        shoppingCart.addToCart(product, quantity);
        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }

    @PutMapping(value = "/{shoppingCartId}/remove/{productId}")
    public ResponseEntity<ShoppingCart> removeFromShoppingCart(@PathVariable Long shoppingCartId, @PathVariable Long productId, @RequestParam Integer quantity) {
        if(!shoppingCartRepository.existsById(shoppingCartId)) return ResponseEntity.notFound().build();
        if(!productRepository.existsById(productId)) return ResponseEntity.badRequest().build();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).get();
        Product product = productRepository.findById(productId).get();

        shoppingCart.removeFromCart(product, quantity);
        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }

    @DeleteMapping(value = "/{shoppingCartId}")
    public ResponseEntity<ShoppingCart> deleteShoppingCart(@PathVariable Long shoppingCartId) {
        if(!shoppingCartRepository.existsById(shoppingCartId)) return ResponseEntity.notFound().build();

        shoppingCartRepository.deleteById(shoppingCartId);
        return ResponseEntity.ok().build();
    }
}
