package com.delivery.project.payment.repository;

import com.delivery.project.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdAndDeletedAtIsNull(UUID orderId);

    @Query("SELECT p FROM Payment p JOIN Order o ON p.orderId = o.id " +
            "WHERE o.customerUsername= :username AND p.deletedAt IS NULL")
    Page<Payment> findAllByUsername(@Param("username") String username, Pageable pageable);

    Optional<Payment> findByOrderId(UUID orderId);
}
