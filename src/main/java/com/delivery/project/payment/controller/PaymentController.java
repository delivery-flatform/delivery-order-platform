package com.delivery.project.payment.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.payment.dto.PaymentConfirmRequestDto;
import com.delivery.project.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // TODO: POST /api/v1/payments          - 결제 처리
    @PostMapping("/confirm")
    @Operation(summary = "결제 처리", description = "결제 요청이 들어오면 데이터 저장후 저장이 완료되었다고 리턴해 줍니다.")
    public ResponseEntity<Boolean> insertPayment(@RequestBody PaymentConfirmRequestDto dto) {

        boolean isSuccess = paymentService.selectPaymentConfirm(dto);
        return ResponseEntity.ok(isSuccess);
    }

    // TODO: GET  /api/v1/payments/{id}     - 결제 단건 조회
    @GetMapping("/searchpayment")
    @Operation(summary = "결제 단건 조회", description = "orderID에 따른 관련 정보를 넘겨줍니다")
    public ResponseEntity<List<PaymentConfirmRequestDto>> selectPayment(@PathVariable UUID orderId) {

        PaymentConfirmRequestDto paymentResponse = paymentService.selectPayment(orderId);
        return ResponseEntity.ok((List<PaymentConfirmRequestDto>) ApiResponse.success(paymentResponse));
    }
    // TODO: GET  /api/v1/payments          - 결제 목록 조회
}
