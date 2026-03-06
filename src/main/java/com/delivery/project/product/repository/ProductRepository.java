package com.delivery.project.product.repository;

import com.delivery.project.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    // 일반 사용자용 (삭제 X + 숨김 X)
    Page<Product> findByDeletedAtIsNullAndIsHiddenFalse(Pageable pageable);

    // 관리자용 (삭제 X, 숨김 포함)
    Page<Product> findByDeletedAtIsNull(Pageable pageable);

    // 단건 조회 (삭제 X)
    //Optional<Product> findByIdAndDeletedAtIsNull(UUID id);
}

