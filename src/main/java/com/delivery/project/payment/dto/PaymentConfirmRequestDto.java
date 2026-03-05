package com.delivery.project.payment.dto;

import com.delivery.project.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequestDto {

    private String PaymentKey;
    private String orderId;
    private int amount;

    public static PaymentConfirmRequestDto from(Payment payment) {
        return PaymentConfirmRequestDto.builder()
                .orderId(String.valueOf(payment.getOrderId()))
                .amount(payment.getAmount())
                .build();
    }
}
