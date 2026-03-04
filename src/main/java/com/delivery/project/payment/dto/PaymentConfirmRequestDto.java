package com.delivery.project.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequestDto {

    private String PaymentKey;
    private String orderId;
    private int amount;
}
