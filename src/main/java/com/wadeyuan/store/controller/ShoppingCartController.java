package com.wadeyuan.store.controller;

import com.wadeyuan.store.domain.CartItem;
import com.wadeyuan.store.domain.Discount;
import com.wadeyuan.store.domain.Product;
import com.wadeyuan.store.domain.ShoppingCart;
import com.wadeyuan.store.dto.ShoppingCartDTO;
import com.wadeyuan.store.repository.DiscountRepository;
import com.wadeyuan.store.repository.ProductRepository;
import com.wadeyuan.store.repository.ShoppingCartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/shopping-carts")
public class ShoppingCartController {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public ShoppingCartController(ShoppingCartRepository shoppingCartRepository, ProductRepository productRepository, DiscountRepository discountRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
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

    @GetMapping(value = "/{shoppingCartId}/calculate")
    public ResponseEntity<ShoppingCartDTO> viewCart(@PathVariable Long shoppingCartId) {
        if(!shoppingCartRepository.existsById(shoppingCartId)) return ResponseEntity.notFound().build();

        ShoppingCart cart = shoppingCartRepository.findById(shoppingCartId).get();
        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
        BigDecimal discountAmount = BigDecimal.valueOf(0.0);
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            List<Discount> discounts = discountRepository.findDiscountsByTargetProductAndEnabledIsTrue(product);

            if (discounts == null || discounts.size() == 0) continue;

            BigDecimal maxDiscount = BigDecimal.valueOf(0.0);
            for (Discount discount : discounts) {
                Product requiredProduct = discount.getRequiredProduct();
                int discountedProducts = 0;
                int requiredQuantity = discount.getRequiredQuantity();
                if (!requiredProduct.equals(product)) {
                    CartItem requiredItem = cart.getCarItemByProduct(requiredProduct);
                    if(requiredItem == null || requiredItem.getQuantity() < requiredQuantity) continue;

                    discountedProducts = item.getQuantity();
                } else {
                    if(item.getQuantity() <= requiredQuantity) continue;
                    discountedProducts = item.getQuantity() - requiredQuantity;
                }

                switch (discount.getDiscountType()) {
                    case AMOUNT -> maxDiscount = maxDiscount.max(discount.getDiscountValue().multiply(BigDecimal.valueOf(discountedProducts)));
                    case PERCENTAGE -> maxDiscount = maxDiscount.max(discount.getDiscountValue().multiply(BigDecimal.valueOf(discountedProducts)).multiply(product.getPrice()).divide(BigDecimal.valueOf(100)));
                }
            }
            discountAmount = discountAmount.add(maxDiscount);
        }

        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setId(cart.getId());
        dto.setItems(cart.getItems());
        dto.setTotalAmount(totalAmount);
        dto.setDiscountAmount(discountAmount);
        dto.setFinalAmount(totalAmount.subtract(discountAmount));

        return ResponseEntity.ok(dto);
    }
}
