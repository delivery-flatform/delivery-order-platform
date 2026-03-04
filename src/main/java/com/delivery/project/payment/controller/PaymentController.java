package com.delivery.project.payment.controller;

import com.delivery.project.payment.dto.PaymentConfirmRequestDto;
import com.delivery.project.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // TODO: POST /api/v1/payments          - 결제 처리
    @PostMapping("/confirm")
    public ResponseEntity<Boolean> insertPayment(@RequestBody PaymentConfirmRequestDto dto) {
        boolean isSuccess = paymentService.selectPaymentConfirm(dto);
        return ResponseEntity.ok(isSuccess);
    }
    // TODO: GET  /api/v1/payments/{id}     - 결제 단건 조회
    // TODO: GET  /api/v1/payments          - 결제 목록 조회
}
