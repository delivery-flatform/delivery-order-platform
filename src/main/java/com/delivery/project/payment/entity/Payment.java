package com.delivery.project.payment.entity;

import com.delivery.project.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

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
    private Status status;

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
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();

        // 결제 취소 시 취소 시간과 취소한 사람 함께 기록
        if (Status.CANCELLED.name().equals(status)) {
            this.deletedAt = LocalDateTime.now();
            this.deletedBy = updatedBy;
        }
    }
}