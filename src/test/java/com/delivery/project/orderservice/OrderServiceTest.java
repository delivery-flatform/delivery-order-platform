package com.delivery.project.orderservice;

import com.delivery.project.order.dto.request.OrderRequestDto;
import com.delivery.project.order.dto.response.OrderResponseDto;
import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderItemRepository;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.order.service.OrderService;
import com.delivery.project.payment.service.PaymentService;
import com.delivery.project.product.entity.Product;
import com.delivery.project.product.repository.ProductRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private PaymentService paymentService;

    @Nested
    @DisplayName("주문 생성 테스트")
    class InsertOrder {

        @Test
        @DisplayName("성공: 유효한 요청 시 주문이 생성된다")
        void insertOrder_Success() {
            String username = "user1";

            OrderRequestDto dto = new OrderRequestDto();
            ReflectionTestUtils.setField(dto, "storeName", "콩나물국밥");
            ReflectionTestUtils.setField(dto, "address", "서울시 강남구");
            ReflectionTestUtils.setField(dto, "productNameList", List.of("콩나물국밥"));

            OrderRequestDto.ProductItem item = new OrderRequestDto.ProductItem();
            ReflectionTestUtils.setField(item, "quantity", 8);
            ReflectionTestUtils.setField(dto, "products", List.of(item));

            Product product = Product.builder().id(UUID.randomUUID()).name("콩나물국밥").price(20000).build();
            Store store = Store.builder().id(UUID.randomUUID()).name("콩나물국밥").build();

            given(storeRepository.findByNameAndDeletedAtIsNull("콩나물국밥")).willReturn(Optional.of(store));
            given(productRepository.findAllByNameInAndDeletedAtIsNull(any())).willReturn(List.of(product));

            Order savedOrder = Order.builder()
                    .id(UUID.randomUUID())
                    .totalPrice(20000)
                    .customerUsername(username)
                    .build();
            given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

            OrderResponseDto result = orderService.insertOrder(username, dto);

            assertThat(result).isNotNull();
            verify(orderItemRepository, times(1)).saveAll(any());
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("실패: 요청한 상품 수와 DB에서 찾은 상품 수가 다를 경우")
        void insertOrder_Fail_ProductMismatch() {
            OrderRequestDto dto = new OrderRequestDto();
            ReflectionTestUtils.setField(dto, "storeName", "치킨집");
            ReflectionTestUtils.setField(dto, "productNameList", List.of("후라이드", "양념치킨"));

            Store store = Store.builder().id(UUID.randomUUID()).name("치킨집").build();
            Product product = Product.builder().name("후라이드").build();

            given(storeRepository.findByNameAndDeletedAtIsNull(any())).willReturn(Optional.of(store));
            given(productRepository.findAllByNameInAndDeletedAtIsNull(any())).willReturn(List.of(product));

            assertThatThrownBy(() -> orderService.insertOrder("user1", dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("일부 상품을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("주문 취소 테스트 (5분 이내)")
    class DeleteOrder {

        @Test
        @DisplayName("성공: 5분 이내 본인 주문 취소 시 결제 취소와 상태 변경이 일어난다")
        void deleteOrder_Success() {
            // given
            UUID orderId = UUID.randomUUID();
            String username = "user1";
            Order order = Order.builder()
                    .id(orderId)
                    .customerUsername(username)
                    .status(Order.Status.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            given(orderRepository.findByIdAndDeletedAtIsNull(orderId)).willReturn(Optional.of(order));
            given(paymentService.deletePayment(orderId, username)).willReturn(true);

            orderService.deleteOrder(orderId, username);
            verify(paymentService).deletePayment(orderId, username);
        }

        @Test
        @DisplayName("실패: 결제사 환불 처리에 실패한 경우")
        void deleteOrder_Fail_RefundFailed() {
            UUID orderId = UUID.randomUUID();
            String username = "user1";
            Order order = Order.builder()
                    .customerUsername(username)
                    .status(Order.Status.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            given(orderRepository.findByIdAndDeletedAtIsNull(orderId)).willReturn(Optional.of(order));
            given(paymentService.deletePayment(orderId, username)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> orderService.deleteOrder(orderId, username))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("결제사 환불 처리에 실패했습니다");
        }
    }
}