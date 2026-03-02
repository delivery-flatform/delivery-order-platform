package com.delivery.project.order.service;

import com.delivery.project.order.dto.request.OrderRequestDto;
import com.delivery.project.order.dto.request.OrderSearchRequestDto;
import com.delivery.project.order.dto.response.OrderResponseDto;
import com.delivery.project.order.entity.Order;
import com.delivery.project.order.entity.OrderItem;
import com.delivery.project.order.repository.OrderItemRepository;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.product.entity.Product;
import com.delivery.project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

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
    @Transactional
    public OrderResponseDto insertOrder(String username, OrderRequestDto dto) {
        // 필수 값 검증
        if (dto.getProducts() == null || dto.getProducts().isEmpty()) {
            throw new IllegalArgumentException("최소 하나 이상의 상품을 선택해야 합니다.");
        }

        // 주문 상품 ID추출
        List<UUID> productIds = dto.getProducts().stream()
                .map(OrderRequestDto.ProductItem::getProductId)
                .toList();

        // 모든 주문 상품 조회 후 products에 저장
        List<Product> products = productRepository.findAllByIdInAndDeletedAtIsNull(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("일부 상품을 찾을 수 없거나 품절되었습니다.");
        }

        // 상품 리스트 순회하며 검증 및 가격 합산
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        int totalOrderPrice = 0;
        for (OrderRequestDto.ProductItem item : dto.getProducts()) {
            Product product = productMap.get(item.getProductId());
            totalOrderPrice += (product.getPrice() * item.getQuantity());
        }

        // Order 엔티티 생성 및 저장
        Order order = Order.builder()
                .customerUsername(username)
                .storeId(dto.getStoreId())
                .totalPrice(totalOrderPrice)
                .status(Order.Status.PENDING) // 내부 Enum 사용
                .deliveryAddress(dto.getAddress())
                .requestNote(dto.getComment())
                .orderType("ONLINE")
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();

        Order savedOrder = orderRepository.save(order);

        // OrderItem 리스트 생성 및 저장
        List<OrderItem> orderItems = dto.getProducts().stream()
                .map(item -> {
                    Product p = productMap.get(item.getProductId());
                    return OrderItem.builder()
                            .orderId(savedOrder.getId())
                            .productName(p.getName())
                            .productPrice(p.getPrice())
                            .quantity(item.getQuantity())
                            .createdAt(LocalDateTime.now())
                            .createdBy(username)
                            .build();
                }).toList();

        orderItemRepository.saveAll(orderItems);

        return OrderResponseDto.from(savedOrder);
    }

    // TODO: 주문 취소 (5분 이내 체크 로직 필요)
    @Transactional
    public OrderResponseDto deleteOrder(UUID orderId, String username) {

        // 주문 존재 여부 및 삭제 여부 확인
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("취소할 주문을 찾을 수 없습니다. ID: " + orderId));

        // 본인의 주문인지 확인 (보안 체크)
        if (!order.getCustomerUsername().equals(username)) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }

        // 이미 취소되었거나 배달 중인지 확인
        if (order.getStatus() != Order.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리 중이거나 취소된 주문은 상태를 변경할 수 없습니다.");
        }

        // 5분 시간 제한 체크
        LocalDateTime now = LocalDateTime.now();
        long minutesPassed = java.time.Duration.between(order.getCreatedAt(), now).toMinutes();
        if (minutesPassed > 5) {
            // 이 메시지가 ApiResponse.fail("에러 메시지")로 전달됩니다.
            throw new IllegalArgumentException("주문 후 5분이 경과하여 취소가 불가능합니다. (경과 시간: " + minutesPassed + "분)");
        }

        // 상태 변경
        order.cancel();

        return OrderResponseDto.from(order);
    }

    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, Order.Status newStatus, String username) {

        // 주문 조회
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("상태를 변경할 주문을 찾을 수 없습니다. ID: " + orderId));

        // 권한 검증
        // 실제로는 토큰에서 권한을 가져오거나, DB에서 해당 상점의 주인인지 조회해야 함
        /*
        if (!order.getStoreOwnerUsername().equals(username)) {
            throw new IllegalArgumentException("해당 상점의 주문 상태를 변경할 권한이 없습니다.");
        }
        */

        // 엔티티 메서드 호출 (상태 변경)
        order.updateStatus(newStatus, username);

        return OrderResponseDto.from(order);
    }
}