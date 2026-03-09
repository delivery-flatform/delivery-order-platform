package com.delivery.project.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    private UUID storeId;
    private String name;
    private String description;
    private Integer price;
}