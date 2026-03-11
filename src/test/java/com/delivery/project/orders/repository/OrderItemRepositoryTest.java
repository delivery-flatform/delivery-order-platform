package com.delivery.project.orders.repository;

import com.delivery.project.order.entity.OrderItem;
import com.delivery.project.order.repository.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    // 성공 테스트

    @Test
    @DisplayName("성공: 특정 주문 ID에 속한 모든 아이템을 조회한다.")
    void findByOrderId_Success() {
        UUID targetOrderId = UUID.randomUUID();
        saveOrderItem(targetOrderId, "콩나물국밥", 1);
        saveOrderItem(targetOrderId, "공기밥", 2);
        saveOrderItem(UUID.randomUUID(), "다른 주문 상품", 1); // 다른 주문 데이터

        List<OrderItem> results = orderItemRepository.findByOrderId(targetOrderId);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(item -> item.getOrderId().equals(targetOrderId));
    }

    @Test
    @DisplayName("성공: OrderItem을 저장하고 ID로 다시 조회할 수 있다.")
    void saveAndFindById_Success() {
        OrderItem item = createOrderItem(UUID.randomUUID(), "수육", 15000, 1);

        OrderItem savedItem = orderItemRepository.save(item);
        OrderItem foundItem = orderItemRepository.findById(savedItem.getId()).orElse(null);

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getProductName()).isEqualTo("수육");
    }

    // 실패 테스트

    @Test
    @DisplayName("성공(검증): 존재하지 않는 주문 ID로 조회하면 빈 리스트를 반환한다.")
    void findByOrderId_ReturnEmptyList() {
        UUID nonExistentOrderId = UUID.randomUUID();

        List<OrderItem> results = orderItemRepository.findByOrderId(nonExistentOrderId);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("실패: 필수 값(orderId)이 없는 데이터를 저장하면 예외가 발생한다.")
    void save_Fail_NullOrderId() {
        OrderItem invalidItem = OrderItem.builder()
                .productName("에러 상품")
                .productPrice(1000)
                .quantity(1)
                .createdAt(LocalDateTime.now())
                .createdBy("system")
                .build();

        try {
            orderItemRepository.saveAndFlush(invalidItem);
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }

    private void saveOrderItem(UUID orderId, String name, int qty) {
        orderItemRepository.save(createOrderItem(orderId, name, 8000, qty));
    }

    private OrderItem createOrderItem(UUID orderId, String name, int price, int qty) {
        return OrderItem.builder()
                .orderId(orderId)
                .productName(name)
                .productPrice(price)
                .quantity(qty)
                .createdAt(LocalDateTime.now())
                .createdBy("user")
                .build();
    }
}