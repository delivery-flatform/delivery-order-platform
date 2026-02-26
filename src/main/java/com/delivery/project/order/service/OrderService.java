package com.delivery.project.order.service;

import com.delivery.project.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // TODO: 주문 목록 조회
    // TODO: 주문 단건 조회
    // TODO: 주문 생성
    // TODO: 주문 취소 (5분 이내)
    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
}
