package com.wadeyuan.store.domain;

import com.wadeyuan.store.constants.DiscountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Product requiredProduct;
    @Min(0)
    private int requiredQuantity;
    @ManyToOne
    private Product targetProduct;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private boolean enabled;

    public Discount() {}

    public Discount(long id, Product requiredProduct, int requiredQuantity, Product targetProduct, DiscountType discountType, BigDecimal discountValue, boolean enabled) {
        this.id = id;
        this.requiredProduct = requiredProduct;
        this.requiredQuantity = requiredQuantity;
        this.targetProduct = targetProduct;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getRequiredProduct() {
        return requiredProduct;
    }

    public void setRequiredProduct(Product requiredProduct) {
        this.requiredProduct = requiredProduct;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(int requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public Product getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(Product targetProduct) {
        this.targetProduct = targetProduct;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return id == discount.id && requiredQuantity == discount.requiredQuantity && discountValue.equals(discount.discountValue) && enabled == discount.enabled && requiredProduct.equals(discount.requiredProduct) && targetProduct.equals(discount.targetProduct) && discountType == discount.discountType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requiredProduct, requiredQuantity, targetProduct, discountType, discountValue, enabled);
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", requiredProduct=" + requiredProduct +
                ", requiredQuantity=" + requiredQuantity +
                ", targetProduct=" + targetProduct +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", enabled=" + enabled +
                '}';
    }
}
