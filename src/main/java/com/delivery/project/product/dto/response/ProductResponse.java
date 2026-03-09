package com.delivery.project.product.dto.response;

import com.delivery.project.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private UUID storeId;
    private String name;
    private String description;
    private Integer price;
    private Boolean isHidden;
    private LocalDateTime createdAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .storeId(product.getStore().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .isHidden(product.getIsHidden())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
