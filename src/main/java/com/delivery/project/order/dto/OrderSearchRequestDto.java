package com.delivery.project.order.dto;

import com.delivery.project.order.entity.Order;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderSearchRequestDto {

    private UUID orderId;
    private UUID storeId;
    private String customerUsername;
    private String productName;
    private String status;

    private Integer minAmount;
    private Integer maxAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static OrderSearchRequestDto from(Order order) {
        return OrderSearchRequestDto.builder()
                .orderId(order.getId())
                .storeId(order.getStoreId())
                .customerUsername(order.getCustomerUsername())
                .productName(order.getProduct() != null ? order.getProduct().getName() : null)
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private Integer totalPrice;
    private LocalDateTime createdAt;
}