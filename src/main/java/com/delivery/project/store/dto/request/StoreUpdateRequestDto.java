package com.delivery.project.store.dto.request;

import lombok.Getter;

@Getter
public class StoreUpdateRequestDto {
    private String storeName;
    private String description;
    private String phone;
    private String address;
    private Integer minOrderPrice;
}
