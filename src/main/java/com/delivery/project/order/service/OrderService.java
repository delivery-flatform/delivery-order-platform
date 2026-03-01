package com.delivery.project.order.service;

import com.delivery.project.order.dto.OrderResponseDto;
import com.delivery.project.order.dto.OrderSearchRequestDto;
import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrderRepository orderRepository;

    // TODO: 주문 전체 조회
    public Page<OrderResponseDto> selectOrders(String userId, String storeId, Pageable pageable) {
        Page<Order> orderPage;

        if (storeId != null && !storeId.isEmpty()) {
            orderPage = orderRepository.findByStoreIdAndDeletedAtIsNull(UUID.fromString(storeId), pageable);
        } else if (userId != null && !userId.isEmpty()) {
            orderPage = orderRepository.findByCustomerUsernameAndDeletedAtIsNull(userId, pageable);
        } else throw new IllegalArgumentException("조회를 위한 userId 또는 storeId가 필요합니다.");

        return orderPage.map(OrderResponseDto::from);
    }

    // TODO: 주문 검색 조회
    public Page<OrderResponseDto> selectOrdersSearch(OrderSearchRequestDto dto, Pageable pageable) {

        if (dto.getStoreId() != null) {
            return orderRepository.searchByStoreIdWithFilters(
                    dto.getStoreId(),
                    dto.getStatus(),
                    dto.getProductName(),
                    dto.getMinAmount(), // DTO 필드명에 맞춰 수정
                    dto.getMaxAmount(),
                    pageable
            ).map(OrderResponseDto::from);
        } else if (dto.getCustomerUsername() != null) {
            return orderRepository.searchByUserIdWithFilters(
                    dto.getCustomerUsername(),
                    dto.getStatus(),
                    dto.getProductName(),
                    dto.getMinAmount(),
                    dto.getMaxAmount(),
                    pageable
            ).map(OrderResponseDto::from);
        } else {
            throw new IllegalArgumentException("검색을 위해 상점 ID 또는 고객 ID가 필요합니다.");
        }
    }

    // TODO: 주문 단건 조회
    public OrderResponseDto selectOrder(UUID orderId) {

        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않거나 삭제되었습니다. ID: " + orderId));

        return OrderResponseDto.from(order);
    }

    // TODO: 주문 생성 (이때는 @Transactional을 붙여야 함)
    // TODO: 주문 취소 (5분 이내 체크 로직 필요)
    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
}