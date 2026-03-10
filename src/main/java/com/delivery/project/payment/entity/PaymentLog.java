package com.delivery.project.payment.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    @Schema(description = "p_payment 테이블의 id를 fk 설정")
    private Payment payment;

    @Column(name = "payment_method", nullable = false, length = 20)
    @Schema(description = "결제 수단")
    private String paymentMethod;

    @Column(nullable = false)
    @Schema(description = "주문한 총 결제 비용")
    private Integer amount;

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

    public void updateStatus(String status, String updatedBy) {
        this.status = status;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();

        // 결제 취소 시 취소 시간과 취소한 사람 함께 기록
        if (Payment.Status.CANCELLED.name().equals(status)) {
            this.deletedAt = LocalDateTime.now();
            this.deletedBy = updatedBy;
        }
    }
}
