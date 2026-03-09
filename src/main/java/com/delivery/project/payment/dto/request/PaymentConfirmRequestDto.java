package com.delivery.project.payment.dto.request;

import com.delivery.project.payment.entity.Payment;
import lombok.*;

@Getter
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequestDto {

    private String PaymentKey;
    private String orderId;
    private int amount;

    public static PaymentConfirmRequestDto from(Payment payment) {
        return PaymentConfirmRequestDto.builder()
                .PaymentKey(payment.getPaymentMethod().name())
                .orderId(String.valueOf(payment.getOrder().getId()))
                .PaymentKey(payment.getPaymentMethod())
                .orderId(String.valueOf(payment.getOrderId()))
                .amount(payment.getAmount())
                .build();
    }
}
