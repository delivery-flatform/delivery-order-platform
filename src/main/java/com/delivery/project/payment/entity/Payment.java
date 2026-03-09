package com.delivery.project.payment.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "결제 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Column(name = "order_id", nullable = false)
    @Schema(description = "주문 ID")
    private UUID orderId;

    @Column(name = "payment_method", nullable = false, length = 100)
    @Schema(description = "결제한 수단")
    private String paymentMethod;

    @Column(name = "paymentKey", nullable = false)
    @Schema(description = "토스페이에서 제공하는 paymentKey저장용")
    private String paymentKey;

    @Column(nullable = false)
    @Schema(description = "주문한 총 결제 비용")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "결제 상태")
    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    public enum PaymentMethod { CARD }
    public enum Status { PENDING, COMPLETED, FAILED, CANCELLED }

    public void updateStatus(String status, String updatedBy) {
        this.status = status;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();

        // 결제 취소 시 취소 시간과 취소한 사람 함께 기록
        if (Status.CANCELLED.name().equals(status)) {
            this.deletedAt = LocalDateTime.now();
            this.deletedBy = updatedBy;
        }
    }
}
