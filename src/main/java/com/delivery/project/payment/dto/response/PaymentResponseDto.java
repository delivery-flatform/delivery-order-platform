package com.delivery.project.payment.dto.response;

import com.delivery.project.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private UUID paymentId;       // 결제 고유 ID
    private UUID orderId;         // 관련 주문 ID
    private String paymentMethod; // 결제 수단 (CARD 등)
    private Integer amount;       // 결제 금액
    private String status;        // 결제 상태 (COMPLETED, FAILED 등)
    private LocalDateTime paidAt; // 결제 완료 시각
    private LocalDateTime createdAt; // 결제 생성 시각

    public static PaymentResponseDto fromEntity(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(String.valueOf(payment.getPaymentMethod()))
                .amount(payment.getAmount())
                .status(String.valueOf(payment.getStatus()))
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}