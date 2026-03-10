package com.delivery.project.review.repository;

import com.delivery.project.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // n+1 관계 해결 : @EntityGraph , JPQL

    @EntityGraph(attributePaths = {"store", "order", "user"})
    Page<Review> findByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"store", "order", "user"})
    Page<Review> findByStoreIdAndDeletedAtIsNullAndContentContaining(UUID id, String search, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByUserUsernameAndDeletedAtIsNull(String userName, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByUserUsernameAndDeletedAtIsNullAndContentContaining(String userName, String search, Pageable pageable);

    boolean existsByOrderId(UUID id);

    // TODO : 가게 평점 조회 (JPQL로 한꺼번에 조회)
    @Query("select coalesce(avg(r.rating), 0) from Review r where r.store.id = :storeId")
    Double findByRatingAvgWhereStoreId(@Param("storeId") UUID storeId);

    Optional<Review> findByIdAndDeletedAtIsNull(UUID id);
}
