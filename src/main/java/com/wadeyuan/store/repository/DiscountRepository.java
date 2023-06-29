package com.wadeyuan.store.repository;

import com.wadeyuan.store.domain.Discount;
import com.wadeyuan.store.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount> {
    List<Discount> findDiscountsByTargetProductAndEnabledIsTrue(Product targetProduct);
}
