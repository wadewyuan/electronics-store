package com.wadeyuan.store.dto;

import com.wadeyuan.store.domain.CartItem;

import java.math.BigDecimal;
import java.util.List;

public class ShoppingCartDTO {
    private long id;
    private List<CartItem> items;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    @Override
    public String toString() {
        return "ShoppingCartDTO{" +
                "id=" + id +
                ", items=" + items +
                ", discountAmount=" + discountAmount +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
