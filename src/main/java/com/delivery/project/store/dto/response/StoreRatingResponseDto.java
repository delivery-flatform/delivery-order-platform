package com.delivery.project.store.dto.response;

import com.delivery.project.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class StoreRatingResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String phone;
    private String address;
    private Integer minOrderPrice;
    private Boolean isOpen;
    private String ownerUsername;
    private String regionName;
    Double rating;

    public static StoreRatingResponseDto from(Store store, Double rating) {
        return StoreRatingResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .phone(store.getPhone())
                .address(store.getAddress())
                .minOrderPrice(store.getMinOrderPrice())
                .isOpen(store.getIsOpen())
                .ownerUsername(store.getUser().getUsername())
                .regionName(store.getRegion().getName())
                .rating(rating)
                .build();
    }
}
