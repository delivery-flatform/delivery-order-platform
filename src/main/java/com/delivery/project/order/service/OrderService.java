package com.delivery.project.order.service;

import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // TODO: 주문 목록 조회
    @Transactional
    public List<Order> findOrderById(String orderId, String storeId) {
        if (storeId == null ) {
            return orderRepository.findByCustomerUsernameAndDeletedAtIsNull(orderId);
        }
        else if (orderId == null ) {
            return orderRepository.findByStoreId(UUID.fromString(storeId));
        }
        else throw new IllegalArgumentException("orderId와 storeId가 모두 없습니다.");
    }
    // TODO: 주문 단건 조회
    // TODO: 주문 생성
    // TODO: 주문 취소 (5분 이내)
    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
}
