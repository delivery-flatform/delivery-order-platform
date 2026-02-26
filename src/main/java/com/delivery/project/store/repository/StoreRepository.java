package com.delivery.project.store.repository;

import com.delivery.project.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    // TODO: 지역별 가게 조회, 카테고리별 가게 조회
}
