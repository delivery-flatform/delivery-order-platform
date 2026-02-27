package com.delivery.project.order.controller;

import com.delivery.project.order.entity.Order;
import com.delivery.project.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "주문 내역" , description = "주문 내역 관리 및 주문 상태 변경")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // TODO: GET    /api/v1/orders          - 주문 목록 조회
    @GetMapping("/list")
    @Operation(summary = "주문 내역 전체 조회" , description = "userId와 storeId로 주문 전체 내역을 조회하는 메서드입니다.")
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam("userId") String userId,
            @RequestParam("storeId") String storeId) {
        List<Order> orderList = orderService.findOrderById(userId, storeId);
        return ResponseEntity.ok(orderList);
    }
    // TODO: GET    /api/v1/orders/{id}     - 주문 단건 조회
    // TODO: POST   /api/v1/orders          - 주문 생성
    // TODO: PATCH  /api/v1/orders/{id}/cancel - 주문 취소
    // TODO: PATCH  /api/v1/orders/{id}/status - 주문 상태 변경
}
