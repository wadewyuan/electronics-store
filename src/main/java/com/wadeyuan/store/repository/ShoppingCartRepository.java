package com.wadeyuan.store.repository;

import com.wadeyuan.store.domain.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long>, JpaSpecificationExecutor<ShoppingCart> {
}
