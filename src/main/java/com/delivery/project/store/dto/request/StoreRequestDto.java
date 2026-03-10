package com.delivery.project.store.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StoreRequestDto {
    private UUID regionId;
    private String storeName;
    private String ownerUsername;
    private String description;
    private String phone;
    private String address;
    private Integer minOrderPrice;
}
