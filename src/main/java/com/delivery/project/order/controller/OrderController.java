package com.delivery.project.order.controller;

import com.delivery.project.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // TODO: GET    /api/v1/orders          - 주문 목록 조회
    // TODO: GET    /api/v1/orders/{id}     - 주문 단건 조회
    // TODO: POST   /api/v1/orders          - 주문 생성
    // TODO: PATCH  /api/v1/orders/{id}/cancel - 주문 취소
    // TODO: PATCH  /api/v1/orders/{id}/status - 주문 상태 변경
}
