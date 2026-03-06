package com.delivery.project.product.repository;

import com.delivery.project.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStoreIdAndDeletedAtIsNull(UUID storeId);

    // 여러 개의 상품 ID를 한꺼번에 조회
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds AND p.deletedAt IS NULL")
    List<Product> findAllByIdInAndDeletedAtIsNull(@Param("productIds") List<UUID> productIds);
}
