package com.wadeyuan.store.dto;

import com.wadeyuan.store.domain.CartItem;

import java.util.List;

public class ShoppingCartDTO {
    private long id;
    private List<CartItem> items;
    private double discountAmount;
    private double totalAmount;
    private double finalAmount;

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

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
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
