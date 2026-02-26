package com.delivery.project.review.repository;

import com.delivery.project.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByStoreIdAndDeletedAtIsNull(UUID storeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.storeId = :storeId AND r.deletedAt IS NULL")
    Double findAverageRatingByStoreId(UUID storeId);
}
