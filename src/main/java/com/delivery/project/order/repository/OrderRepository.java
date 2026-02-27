package com.delivery.project.order.repository;

import com.delivery.project.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // 고객 이름으로 조회
    @Query(value = "select o from Order o " +
            "join fetch o.customer u " +
            "join fetch o.product p " +
            "where o.customerUsername = :customerUsername",
            countQuery = "select count(o) from Order o where o.customerUsername = :customerUsername")
    Page<Order> findByCustomerUsernameAndDeletedAtIsNull(@Param("customerUsername") String customerUsername, Pageable pageable);

    // 상점 ID로 조회
    @Query(value = "select o from Order o " +
            "join fetch o.customer u " +
            "join fetch o.product p " +
            "where o.storeId = :storeId",
            countQuery = "select count(o) from Order o where o.storeId = :storeId")
    Page<Order> findByStoreId(@Param("storeId") UUID storeId, Pageable pageable);
}