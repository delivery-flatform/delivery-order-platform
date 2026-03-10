package com.delivery.project.orders.entity;

import com.delivery.project.order.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .status(Order.Status.READY)
                .customerUsername("test_user")
                .build();
    }

    // 성공 테스트

    @Test
    @DisplayName("성공: READY 상태에서 주문을 취소하면 CANCELLED 상태가 된다.")
    void cancel_Success() {

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(Order.Status.CANCELLED);
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("성공: 정상적인 상태 흐름(READY -> PENDING -> CONFIRMED)은 허용된다.")
    void updateStatus_Success_Flow() {
        order.updateStatus(Order.Status.PENDING, "admin");
        assertThat(order.getStatus()).isEqualTo(Order.Status.PENDING);

        order.updateStatus(Order.Status.CONFIRMED, "admin");
        assertThat(order.getStatus()).isEqualTo(Order.Status.CONFIRMED);

        assertThat(order.getUpdatedBy()).isEqualTo("admin");
    }

    // 실패 테스트

    @Test
    @DisplayName("실패: 이미 취소된 주문을 다시 취소하려고 하면 예외가 발생한다.")
    void cancel_Fail_AlreadyCancelled() {

        order.cancel();

        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 취소된 주문입니다.");
    }

    @Test
    @DisplayName("실패: 허용되지 않는 상태 전이(READY -> COMPLETED) 시 예외가 발생한다.")
    void updateStatus_Fail_InvalidTransition() {

        assertThatThrownBy(() -> order.updateStatus(Order.Status.COMPLETED, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상태를 변경할 수 없습니다: [READY] -> [COMPLETED]");
    }
}