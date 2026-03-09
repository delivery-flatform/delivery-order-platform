package com.delivery.project.payment.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.payment.dto.request.PaymentConfirmRequestDto;
import com.delivery.project.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "결제 처리", description = "주문을 확인 결제하고 결재 정보 저장 및 주문 상태 변경")
    public ResponseEntity<ApiResponse<Boolean>> insertPayment(@RequestBody PaymentConfirmRequestDto dto) {
        boolean isSuccess = paymentService.selectPaymentConfirm(dto);

        // 성공/실패 여부에 따라 공통 응답 객체에 담아서 반환
        if (isSuccess) {
            return ResponseEntity.ok(ApiResponse.success(true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail("결제 승인 실패"));
        }
    }

    // TODO: GET  /api/v1/payments/{id}     - 결제 단건 조회

    // TODO: GET  /api/v1/payments          - 결제 목록 조회
}
