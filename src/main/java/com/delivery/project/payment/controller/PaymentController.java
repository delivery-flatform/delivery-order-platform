package com.delivery.project.payment.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.payment.dto.request.PaymentConfirmRequestDto;
import com.delivery.project.payment.dto.response.PaymentResponseDto;
import com.delivery.project.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @GetMapping("/searchpayment")
    @Operation(summary = "결제 단건 조회", description = "주문 단건 별로 조회")
    public ResponseEntity<ApiResponse<PaymentConfirmRequestDto>> selectPayment(@RequestParam("orderId") UUID orderId) {

        PaymentConfirmRequestDto paymentResponse = paymentService.selectPayment(orderId);
        return ResponseEntity.ok(ApiResponse.success(paymentResponse));
    }

    // TODO: GET  /api/v1/payments          - 결제 목록 조회
    @GetMapping("/searchpaymentlist")
    @Operation(summary = "결제 목록 조회", description = "주문 목록 전체 조회")
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
