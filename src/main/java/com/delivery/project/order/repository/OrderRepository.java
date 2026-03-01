package com.delivery.project.order.repository;

import com.delivery.project.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * 1. 고객 ID 기반 단순 목록 조회
     */
    @Query("select o from Order o join fetch o.customer u join fetch o.product p " +
            "where o.customerUsername = :customerUsername and o.deletedAt is null")
    Page<Order> findByCustomerUsernameAndDeletedAtIsNull(@Param("customerUsername") String customerUsername, Pageable pageable);

    /**
     * 2. 상점 ID 기반 단순 목록 조회
     */
    @Query("select o from Order o join fetch o.customer u join fetch o.product p " +
            "where o.storeId = :storeId and o.deletedAt is null")
    Page<Order> findByStoreIdAndDeletedAtIsNull(@Param("storeId") UUID storeId, Pageable pageable);

    /**
     * 3. [검색] 고객 ID 기반 복합 필터 검색
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.product p " +
            "WHERE o.customerUsername = :userId " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice) " +
            "AND o.deletedAt IS NULL")
    Page<Order> searchByUserIdWithFilters(
            @Param("userId") String userId,
            @Param("status") String status,
            @Param("productName") String productName,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    /**
     * 4. [검색] 상점 ID 기반 복합 필터 검색
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.customer u " +
            "LEFT JOIN FETCH o.product p " +
            "WHERE o.storeId = :storeId " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice) " +
            "AND o.deletedAt IS NULL")
    Page<Order> searchByStoreIdWithFilters(
            @Param("storeId") UUID storeId,
            @Param("status") String status,
            @Param("productName") String productName,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    /**
     * 5. 주문 ID로 단건 조회
     */
    Optional<Order> findByIdAndDeletedAtIsNull(UUID orderId);

}