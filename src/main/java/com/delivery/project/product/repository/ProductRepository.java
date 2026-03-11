package com.delivery.project.product.repository;

import com.delivery.project.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByStoreIdAndDeletedAtIsNull(UUID storeId);

    @Query("SELECT p FROM Product p WHERE p.name IN :productNames AND p.deletedAt IS NULL")
    List<Product> findAllByNameInAndDeletedAtIsNull(@Param("productNames") List<String> productNames);

    Page<Product> findByDeletedAtIsNull(Pageable pageable);

    Page<Product> findByDeletedAtIsNullAndIsHiddenFalse(Pageable pageable);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);
}

