package com.delivery.project.product.dto.response;

import com.delivery.project.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

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
        return new ProductResponse(
                product.getId(),
                product.getStore().getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getIsHidden(),
                product.getCreatedAt()
        );
    }
}
