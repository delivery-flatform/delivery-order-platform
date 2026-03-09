package com.delivery.project.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private String storeName;
    private String address;
    private String comment;
    private List<ProductItem> products; // 상품id
    private List<String> productNameList; // 상품한글이름
    private Integer amount;

    @Getter
    @NoArgsConstructor
    public static class ProductItem {
        private UUID productId;
        private Integer quantity;
    }

}