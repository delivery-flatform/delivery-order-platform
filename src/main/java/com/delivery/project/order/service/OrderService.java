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
    public OrderResponseDto createOrder(String username, OrderRequestDto dto) {
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
    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
}