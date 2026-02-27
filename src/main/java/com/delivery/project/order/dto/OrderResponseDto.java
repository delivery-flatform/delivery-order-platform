package com.delivery.project.order.dto;

import com.delivery.project.order.entity.Order;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponseDto {

    private UUID orderId;
    private String customerUsername;
    private String productName;
    private Integer totalPrice;
    private String status;
    private LocalDateTime createdAt;

    public static OrderResponseDto from(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .customerUsername(order.getCustomerUsername())
                .productName(order.getProduct() != null ? order.getProduct().getName() : "Unknown")
                .totalPrice(order.getTotalPrice())
                .status(String.valueOf(order.getStatus()))
                .createdAt(order.getCreatedAt())
                .build();
    }
}