package com.delivery.project.payment.service;

import com.delivery.project.payment.dto.request.PaymentConfirmRequestDto;
import com.delivery.project.payment.dto.response.PaymentResponseDto;
import com.delivery.project.payment.entity.Payment;
import com.delivery.project.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // TODO: 결제 처리 (카드만, DB 기록)
    // TODO: 결제 단건 조회
    // TODO: 결제 목록 조회

    // TODO: 결제 단건 조회
    public PaymentConfirmRequestDto selectPayment(UUID orderId) {

        Payment payment = paymentRepository.findByOrder_IdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않거나 삭제되었습니다. ID: " + orderId));

        return PaymentConfirmRequestDto.from(payment);
    }

    // TODO: 결제 목록 조회
    public Page<PaymentResponseDto> getPaymentList(String username, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByUsername(username, pageable);

        return payments.map(PaymentResponseDto::fromEntity);
    }
}
