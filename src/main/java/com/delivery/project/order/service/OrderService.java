package com.delivery.project.order.service;

import com.delivery.project.order.dto.request.OrderRequestDto;
import com.delivery.project.order.dto.request.OrderSearchRequestDto;
import com.delivery.project.order.dto.response.OrderResponseDto;
import com.delivery.project.order.entity.Order;
import com.delivery.project.order.entity.OrderItem;
import com.delivery.project.order.repository.OrderItemRepository;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.payment.service.PaymentService;
import com.delivery.project.product.entity.Product;
import com.delivery.project.product.repository.ProductRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PaymentService paymentService;

    // TODO: 주문 전체 조회
    //@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER','CUSTOMER')")
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
    //@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER','CUSTOMER')")
    public Page<OrderResponseDto> selectOrdersSearch(OrderSearchRequestDto dto, Pageable pageable) {

        if (dto.getStoreId() != null) {
            return orderRepository.searchByStoreIdWithFilters(
                    dto.getStoreId(),
                    dto.getStatus(),
                    dto.getProductName(),
                    dto.getMinAmount(),
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
    //@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER', 'CUSTOMER')")
    public OrderResponseDto selectOrder(UUID orderId) {

        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않거나 삭제되었습니다. ID: " + orderId));

        return OrderResponseDto.from(order);
    }

    // TODO: 주문 생성 (이때는 @Transactional을 붙여야 함)
    @Transactional
    //@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'CUSTOMER')")
    public OrderResponseDto insertOrder(String username, OrderRequestDto dto) {
        // 가게 이름으로 Store 엔티티 조회
        String storeName = dto.getStoreName();
        Store store = storeRepository.findByNameAndDeletedAtIsNull(storeName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다: " + storeName));

        // 상품 정보 조회
        List<String> requestNames = dto.getProductNameList();
        List<Product> foundProducts = productRepository.findAllByNameInAndDeletedAtIsNull(requestNames);

        //요청한 상품 수와 DB에서 찾은 상품 수가 다르면 에러 처리
        if (foundProducts.size() != requestNames.size()) {
            throw new IllegalArgumentException("일부 상품을 찾을 수 없습니다. 요청: " + requestNames.size() + "건");
        }

        // 상품 정보를 Map으로 변환 (이름 -> Product 객체)하여 매핑 속도 최적화
        Map<String, Product> productMapByName = foundProducts.stream()
                .collect(Collectors.toMap(Product::getName, p -> p));

        // 총 금액 계산
        int calculatedTotalAmount = 0;
        for (int i = 0; i < requestNames.size(); i++) {
            String name = requestNames.get(i);
            Integer quantity = dto.getProducts().get(i).getQuantity();
            Product p = productMapByName.get(name);
            if (p == null) throw new IllegalArgumentException("상품 매칭 실패: " + name);
            calculatedTotalAmount += (p.getPrice() * quantity);
        }

        // p_order 테이블 먼저 생성 및 저장
        // ID가 외래키이므로 부모인 Order가 먼저 DB에 들어가야 합니다.
        Order order = Order.builder()
                .customerUsername(username)
                .storeId(store.getId()) // 이름으로 찾은 실제 Store UUID 저장
                .productId(foundProducts.get(0).getId()) // 대표 상품 ID
                .totalPrice(calculatedTotalAmount)
                .status(Order.Status.READY)
                .deliveryAddress(dto.getAddress())
                .requestNote(dto.getComment())
                .orderType("ONLINE")
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();

        Order savedOrder = orderRepository.save(order);

        // p_order_item 리스트 생성 (발급된 order_id 사용)
        List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (int i = 0; i < requestNames.size(); i++) {
            String name = requestNames.get(i);
            Integer quantity = dto.getProducts().get(i).getQuantity();
            Product p = productMapByName.get(name);

            orderItems.add(OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .productName(p.getName())
                    .productPrice(p.getPrice())
                    .quantity(quantity)
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .build());
        }

        // 자식 테이블들 저장
        orderItemRepository.saveAll(orderItems);

        // 생성된 주문 정보를 반환 (이 ID를 프론트에서 Toss orderId로 사용)
        return OrderResponseDto.from(savedOrder);
    }

    // TODO: 주문 취소 (5분 이내 체크 로직 필요)
    @Transactional
    //@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'CUSTOMER','OWNER')")
    public OrderResponseDto deleteOrder(UUID orderId, String username) {

        // 주문 존재 여부 및 삭제 여부 확인
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("취소할 주문을 찾을 수 없습니다. ID: " + orderId));

        // 본인의 주문인지 확인 (보안 체크)
        if (!order.getCustomerUsername().equals(username)) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }

        // 이미 취소되었거나 배달 중인지 확인
        if (!String.valueOf(order.getStatus()).equals("PENDING")) {
            throw new IllegalArgumentException("이미 처리 중이거나 취소된 주문은 상태를 변경할 수 없습니다.");
        }

        // 5분 시간 제한 체크
        LocalDateTime now = LocalDateTime.now();
        long minutesPassed = java.time.Duration.between(order.getCreatedAt(), now).toMinutes();
        if (minutesPassed > 5) {
            // 이 메시지가 ApiResponse.fail("에러 메시지")로 전달됩니다.
            throw new IllegalArgumentException("주문 후 5분이 경과하여 취소가 불가능합니다. (경과 시간: " + minutesPassed + "분)");
        }

        boolean isCancelled = paymentService.deletePayment(orderId, username);

        if (!isCancelled) {
            throw new RuntimeException("결제사 환불 처리에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }

        // 상태 변경
        order.cancel();
        orderRepository.save(order);

        return OrderResponseDto.from(order);
    }

    // TODO: 주문 상태 변경 (OWNER or MANAGER+)
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER','OWNER')")
    public OrderResponseDto updateOrderStatus(UUID orderId, Order.Status newStatus, String username, List<String> roles) {

        // 주문 조회
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("상태를 변경할 주문을 찾을 수 없습니다. ID: " + orderId));

        // MASTER나 MANAGER 권한이 포함되어 있는지 확인
        boolean isStaff = roles.contains("ROLE_MASTER") || roles.contains("ROLE_MANAGER");

        if (!isStaff) {
            // 사장님(OWNER)인 경우에만 본인 가게인지 확인
            Store store = storeRepository.findByUserUsernameAndDeletedAtIsNull(username)
                    .orElseThrow(() -> new IllegalArgumentException("운영 중인 가게 정보를 찾을 수 없습니다: " + username));

            if (!order.getStoreId().equals(store.getId())) {
                throw new IllegalArgumentException("본인 가게의 주문만 상태를 변경할 수 있습니다.");
            }
        }

        order.updateStatus(newStatus, username);

        return OrderResponseDto.from(order);
    }
}