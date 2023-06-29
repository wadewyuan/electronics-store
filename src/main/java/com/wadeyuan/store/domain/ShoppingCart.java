package com.wadeyuan.store.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<CartItem> items;

    public ShoppingCart() {}

    public ShoppingCart(long id, List<CartItem> items) {
        this.id = id;
        this.items = items;
    }

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

    public void addToCart(Product product, int quantity) {
        CartItem cartItem = items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);
        if(cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return;
        }
        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        items.add(cartItem);
    }

    public void removeFromCart(Product product, int quantity) {
        CartItem cartItem = items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);
        if(cartItem == null) return;

        int newQuantity = cartItem.getQuantity() - quantity;
        if(newQuantity <= 0) {
            items.remove(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart that = (ShoppingCart) o;
        return id == that.id && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, items);
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "id=" + id +
                ", items=" + items +
                '}';
    }
}
