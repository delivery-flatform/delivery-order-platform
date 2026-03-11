package com.delivery.project.store.repository;

import com.delivery.project.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
    Optional<StoreCategory> findByStoreIdAndCategoryId(UUID storeId, UUID categoryId);

    boolean existsByStore_IdAndCategory_Id(UUID storeId, UUID categoryId);
}
