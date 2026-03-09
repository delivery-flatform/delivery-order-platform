package com.delivery.project.payment.service;

import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.payment.dto.request.PaymentConfirmRequestDto;
import com.delivery.project.payment.dto.response.PaymentResponseDto;
import com.delivery.project.payment.entity.Payment;
import com.delivery.project.payment.entity.PaymentLog;
import com.delivery.project.payment.repository.PaymentLogRepository;
import com.delivery.project.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentLogRepository paymentLogReposity;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String TOSS_SECRET_KEY = "test_sk_mBZ1gQ4YVXK4AxDjYdx93l2KPoqN";

    @Transactional
    public boolean selectPaymentConfirm(PaymentConfirmRequestDto dto) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        HttpHeaders headers = new HttpHeaders();

        // trim() 추가 및 명확한 시크릿 키 사용 확인
        String secret = TOSS_SECRET_KEY.trim();
        String auth = secret + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("보내는 요청 데이터: paymentKey={}, orderId={}, amount={}", dto.getPaymentKey(), dto.getOrderId(), dto.getAmount());

        // 요청 파라미터 구성
        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", dto.getPaymentKey());
        params.put("orderId", dto.getOrderId());
        params.put("amount", dto.getAmount());

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 주문(Order) 상태 업데이트
                // dto.getOrderId()는 우리 DB의 주문 UUID입니다.
                Order order = orderRepository.findById(UUID.fromString(dto.getOrderId()))
                        .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

                // 결제(Payment) 내역 DB 저장
                Payment payment = Payment.builder()
                        .order(order)
                        .paymentMethod(String.valueOf(Payment.PaymentMethod.CARD))
                        .paymentKey(dto.getPaymentKey())
                        .amount(dto.getAmount())
                        .status(Payment.Status.COMPLETED) // 토스 승인 완료 상태
                        .createdAt(LocalDateTime.now())
                        .createdBy(order.getCustomerUsername())
                        .build();
                paymentRepository.save(payment);

                // 결제 기록 저장
                PaymentLog paymentLog = PaymentLog.builder()
                        .payment(payment)
                        .paymentMethod(String.valueOf(Payment.PaymentMethod.CARD))
                        .amount(dto.getAmount())
                        .status(Payment.Status.COMPLETED.name())
                        .createdAt(LocalDateTime.now())
                        .createdBy(order.getCustomerUsername())
                        .build();
                paymentLogReposity.save(paymentLog);

                // 결제 완료 상태로 변경
                order.updateStatus(Order.Status.PENDING, order.getCustomerUsername());
                orderRepository.save(order);

                log.info("결제 승인 및 DB 저장 성공: orderId={}", dto.getOrderId());
                return true;
            }
            return false;
        } catch (HttpStatusCodeException e) {
            Order order = orderRepository.findById(UUID.fromString(dto.getOrderId()))
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            // 결제 실패 저장
            Payment payment = Payment.builder()
                    .paymentMethod(String.valueOf(Payment.PaymentMethod.CARD))
                    .paymentKey(dto.getPaymentKey())
                    .amount(dto.getAmount())
                    .status(Payment.Status.FAILED) // 토스 승인 실패 상태
                    .createdAt(LocalDateTime.now())
                    .createdBy(order.getCustomerUsername())
                    .build();
            paymentRepository.save(payment);


            log.error("토스 승인 거절 사유: {}", e.getResponseBodyAsString());
            return false;
        }
    }

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

    // TODO: 결제 취소
    @Transactional
    public boolean deletePayment(UUID orderId, String username) {
        // 해당 주문의 결제 내역 조회 (paymentKey가 필요함)
        Payment payment = paymentRepository.findByOrder_IdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

        PaymentLog paymentLog = paymentLogReposity.findByPaymentIdAndDeletedAtIsNull(payment.getId())
                .orElseThrow(() -> new IllegalArgumentException("수정할 결제 로그를 찾을 수 없습니다."));


        String url = "https://api.tosspayments.com/v1/payments/" + payment.getPaymentKey() + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        String auth = TOSS_SECRET_KEY.trim() + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("cancelReason", "사용자 단순 변심"); // 토스페이 취소시 취소 사유가 필수값

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                payment.updateStatus(Payment.Status.CANCELLED.name(), username);
                paymentLog.updateStatus(Payment.Status.CANCELLED.name(), username);

                log.info("토스 결제 취소 성공: orderId={}", orderId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("토스 결제 취소 실패: {}", e.getMessage());
            throw new RuntimeException("결제 취소 중 오류가 발생했습니다.");
        }
    }
}
