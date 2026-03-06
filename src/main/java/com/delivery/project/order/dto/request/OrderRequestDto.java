package com.delivery.project.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private UUID storeId;
    private String address;
    private String comment;
    private List<ProductItem> products;

    @Getter
    @NoArgsConstructor
    public static class ProductItem {
        private UUID productId;
        private Integer quantity;
    }
}