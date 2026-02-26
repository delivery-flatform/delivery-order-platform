package com.delivery.project.payment.service;

import com.delivery.project.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // TODO: 결제 처리 (카드만, DB 기록)
    // TODO: 결제 단건 조회
    // TODO: 결제 목록 조회
}
