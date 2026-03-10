package com.delivery.project.payment.repository;

import com.delivery.project.payment.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID> {

    Optional<PaymentLog> findByPaymentIdAndDeletedAtIsNull(UUID paymentId);
}
