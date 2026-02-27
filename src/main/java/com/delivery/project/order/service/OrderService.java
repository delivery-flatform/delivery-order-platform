package com.delivery.project.order.service;

import com.delivery.project.order.dto.OrderResponseDto;
import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<OrderResponseDto> selectOrders(String userId, String storeId, Pageable pageable) {
        Page<Order> orderPage;

        if (storeId != null && !storeId.isEmpty()) {
            orderPage = orderRepository.findByStoreId(UUID.fromString(storeId), pageable);
        } else if (userId != null && !userId.isEmpty()) {
            orderPage = orderRepository.findByCustomerUsernameAndDeletedAtIsNull(userId, pageable);
        } else throw new IllegalArgumentException("조회를 위한 userId 또는 storeId가 필요합니다.");

        return orderPage.map(OrderResponseDto::from);
    }

    // TODO: 주문 단건 조회
    // TODO: 주문 생성 (이때는 @Transactional을 붙여야 함)
    // TODO: 주문 취소 (5분 이내 체크 로직 필요)
    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
}