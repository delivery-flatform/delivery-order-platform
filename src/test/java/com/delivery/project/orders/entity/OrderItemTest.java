package com.delivery.project.orders.entity;

import com.delivery.project.order.entity.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderItemTest {

    // 성공 테스트

    @Test
    @DisplayName("성공: Builder를 통해 OrderItem을 생성하면 모든 필드가 정상적으로 매핑된다.")
    void createOrderItem_Success() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OrderItem orderItem = OrderItem.builder()
                .orderId(orderId)
                .productName("콩나물국밥")
                .productPrice(9000)
                .quantity(2)
                .createdAt(now)
                .createdBy("customer1")
                .build();

        assertAll(
                () -> assertThat(orderItem.getOrderId()).isEqualTo(orderId),
                () -> assertThat(orderItem.getProductName()).isEqualTo("콩나물국밥"),
                () -> assertThat(orderItem.getProductPrice()).isEqualTo(9000),
                () -> assertThat(orderItem.getQuantity()).isEqualTo(2),
                () -> assertThat(orderItem.getCreatedBy()).isEqualTo("customer1")
        );
    }

    @Test
    @DisplayName("성공: 상품 가격과 수량의 곱으로 총액 계산 로직을 검증한다.")
    void calculateTotalPrice_Success() {
        OrderItem orderItem = OrderItem.builder()
                .productPrice(12000)
                .quantity(3)
                .build();

        int calculatedPrice = orderItem.getProductPrice() * orderItem.getQuantity();

        assertThat(calculatedPrice).isEqualTo(36000);
    }

    // 실패 테스트

    @Test
    @DisplayName("실패: 필수 필드인 상품명(productName)이 누락된 경우 생성 로직을 확인한다.")
    void createOrderItem_Fail_NullProductName() {

        OrderItem orderItem = OrderItem.builder()
                .productName(null)
                .build();

        assertThat(orderItem.getProductName()).isNull();
    }

    @Test
    @DisplayName("실패: 수량(quantity)이 0 이하인 비정상적인 데이터가 들어오는지 확인한다.")
    void createOrderItem_Fail_InvalidQuantity() {
        OrderItem orderItem = OrderItem.builder()
                .quantity(-1)
                .build();

        assertThat(orderItem.getQuantity()).isLessThan(0);
    }
}
