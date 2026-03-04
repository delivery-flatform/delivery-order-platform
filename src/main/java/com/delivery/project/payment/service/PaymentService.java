package com.delivery.project.payment.service;

import com.delivery.project.payment.dto.PaymentConfirmRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String TOSS_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    public boolean selectPaymentConfirm(PaymentConfirmRequestDto dto) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        // 헤더 설정: 인증 정보 및 컨텐츠 타입
        HttpHeaders headers = new HttpHeaders();
        String encodedAuth = Base64.getEncoder().encodeToString(TOSS_SECRET_KEY.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 파라미터 구성
        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", dto.getPaymentKey());
        params.put("orderId", dto.getOrderId());
        params.put("amount", dto.getAmount());

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("토스 결제 승인 실패: {}", e.getMessage());
            return false;
        }
    }
    // TODO: 결제 처리 (카드만, DB 기록)
    // TODO: 결제 단건 조회
    // TODO: 결제 목록 조회
}
