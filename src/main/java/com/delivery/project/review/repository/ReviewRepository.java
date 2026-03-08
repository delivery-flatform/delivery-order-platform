package com.delivery.project.review.repository;

import com.delivery.project.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query("""
        select coalesce(avg(r.rating),0)
        from Review r
        where r.store.id = :storeId
        and r.deletedAt is null
    """)
    Double findByRatingAvgWhereStoreId(@Param("storeId") UUID storeId);
}
