package com.delivery.project.payment.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.payment.dto.request.PaymentConfirmRequestDto;
import com.delivery.project.payment.dto.response.PaymentResponseDto;
import com.delivery.project.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // TODO: POST /api/v1/payments          - 결제 처리
    // TODO: GET  /api/v1/payments/{id}     - 결제 단건 조회
    // TODO: GET  /api/v1/payments          - 결제 목록 조회

    // TODO: GET  /api/v1/payments/{id}     - 결제 단건 조회
    @GetMapping("/searchpayment/{orderId}")
    public ResponseEntity<ApiResponse<PaymentConfirmRequestDto>> selectPayment(@PathVariable UUID orderId) {

        PaymentConfirmRequestDto paymentResponse = paymentService.selectPayment(orderId);
        return ResponseEntity.ok(ApiResponse.success(paymentResponse));
    }

    // TODO: GET  /api/v1/payments          - 결제 목록 조회
    @GetMapping("/searchpaymentlist")
    public ResponseEntity<ApiResponse<Page<PaymentResponseDto>>> selectPaymentList(
            // 시큐리티를 사용 중이라면 인증 객체에서 username을 가져옵니다.
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Pageable validatedPageable = PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );

        String username = userDetails.getUsername(); // 로그인한 userid 추출
        Page<PaymentResponseDto> response = paymentService.getPaymentList(username, validatedPageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


}
