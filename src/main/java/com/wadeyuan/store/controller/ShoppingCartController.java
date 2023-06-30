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

    @PutMapping(value = "/{shoppingCartId}/clear")
    public ResponseEntity<ShoppingCart> clearCart(@PathVariable Long shoppingCartId) {
        if(!shoppingCartRepository.existsById(shoppingCartId)) return ResponseEntity.notFound().build();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).get();
        shoppingCart.getItems().clear();
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
        BigDecimal totalAmount = BigDecimal.ZERO; // Calculated by all cart items original price
        BigDecimal discountAmount = BigDecimal.ZERO; // Calculated by all cart items discount (if any)

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            // Accumulate to the total amount of the entire cart
            BigDecimal cartItemAmount = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())); // Amount of current cart item
            totalAmount = totalAmount.add(cartItemAmount);

            // Accumulate to the total discount amount of the entire cart, additional comparison to the cartItemAmount, because the discount of one item shouldn't exceed its amount
            BigDecimal cartItemDiscount = calculateDiscountOfCartItem(item, cart);
            discountAmount = discountAmount.add(cartItemDiscount.min(cartItemAmount));
        }

        ShoppingCartDTO dto = new ShoppingCartDTO(cart.getId(), cart.getItems());
        dto.setTotalAmount(totalAmount);
        dto.setDiscountAmount(discountAmount);
        dto.setFinalAmount(totalAmount.subtract(discountAmount));

        return ResponseEntity.ok(dto);
    }

    private BigDecimal calculateDiscountOfCartItem(CartItem item, ShoppingCart cart) {
        // Find all discounts that are applied to the current product
        List<Discount> discounts = discountRepository.findDiscountsByTargetProductAndEnabledIsTrue(item.getProduct());

        return discounts.stream()
                .map(discount -> calculateDiscountAmountFromDiscountRule(item, cart, discount)) // Calculate the amount of each discount rule applied to current item
                .max(BigDecimal::compareTo) // We choose the maximum discount for the item
                .orElse(BigDecimal.ZERO); // If nothing find, return zero
    }

    private BigDecimal calculateDiscountAmountFromDiscountRule(CartItem item, ShoppingCart cart, Discount discount) {
        Product product = item.getProduct();
        Product requiredProductInCart = discount.getRequiredProduct();
        BigDecimal discountAmount = BigDecimal.ZERO;

        int discountableProductQuantity; // To calculate the number of discountable products in current cart item
        int requiredQuantity = discount.getRequiredQuantity();
        if (!requiredProductInCart.equals(product)) {
            // If the discount rule requires some other products in cart, we'll check whether there are sufficient number of required product to apply discount
            CartItem requiredItem = cart.getCarItemByProduct(requiredProductInCart);
            if(requiredItem == null || requiredItem.getQuantity() < requiredQuantity) return discountAmount;

            discountableProductQuantity = item.getQuantity();
        } else {
            // If the discount rule requires the same product (typically by x and the remaining y can get z% off), we'll calculate the discountable products
            if(item.getQuantity() <= requiredQuantity) return discountAmount;

            discountableProductQuantity = item.getQuantity() - requiredQuantity;
        }
        // Calculate the discount amount
        switch (discount.getDiscountType()) {
            case AMOUNT -> discountAmount = discount.getDiscountValue();
            case PERCENTAGE -> discountAmount = discount.getDiscountValue().multiply(BigDecimal.valueOf(discountableProductQuantity)).multiply(product.getPrice()).divide(BigDecimal.valueOf(100));
        }

        return discountAmount;
    }
}
