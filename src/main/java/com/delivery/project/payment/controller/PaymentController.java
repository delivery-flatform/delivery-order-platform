package com.delivery.project.payment.controller;

import com.delivery.project.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // TODO: POST /api/v1/payments          - 결제 처리
    // TODO: GET  /api/v1/payments/{id}     - 결제 단건 조회
    // TODO: GET  /api/v1/payments          - 결제 목록 조회
}
