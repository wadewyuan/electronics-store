package com.wadeyuan.store.controller;

import com.wadeyuan.store.constants.DiscountType;
import com.wadeyuan.store.domain.Discount;
import com.wadeyuan.store.domain.Product;
import com.wadeyuan.store.repository.DiscountRepository;
import com.wadeyuan.store.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/discounts")
public class DiscountController {
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    public DiscountController(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        discount.setEnabled(true);
        Discount createdDiscount = discountRepository.save(discount);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDiscount.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdDiscount);
    }

    @PostMapping(value = "/simpleDiscount/product/{productId}")
    public ResponseEntity<Discount> createSimpleDiscount(@RequestParam Integer requiredQuantity, @RequestParam Double percentageOff, @PathVariable Long productId) {
        if(!productRepository.existsById(productId)) return ResponseEntity.badRequest().build();

        /*
            Compose a simple discount:
            required product is the same one with target product, quantity and percentage off coming from request params
            So that administrator can easily apply a buy x and y% off to the remaining products
         */
        Product product = productRepository.findById(productId).get();
        Discount simpleDiscount = new Discount();
        simpleDiscount.setDiscountType(DiscountType.PERCENTAGE);
        simpleDiscount.setDiscountValue(percentageOff);
        simpleDiscount.setEnabled(true);
        simpleDiscount.setRequiredProduct(product);
        simpleDiscount.setRequiredQuantity(requiredQuantity);
        simpleDiscount.setTargetProduct(product);
        return createDiscount(simpleDiscount);
    }

    @DeleteMapping(value = "/{discountId}")
    public ResponseEntity<Discount> deleteDiscount(@PathVariable Long discountId) {
        if(!discountRepository.existsById(discountId)) return ResponseEntity.notFound().build();

        discountRepository.deleteById(discountId);
        return ResponseEntity.ok().build();
    }
}
