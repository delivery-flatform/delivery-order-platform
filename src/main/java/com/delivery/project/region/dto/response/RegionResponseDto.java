package com.delivery.project.region.dto.response;

import com.delivery.project.region.entity.Region;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RegionResponseDto {

    private UUID id;
    private String name;
    private String city;
    private String district;
    private boolean isActive;

    public static RegionResponseDto from(Region region) {
        return RegionResponseDto.builder()
                .id(region.getId())
                .name(region.getName())
                .city(region.getCity())
                .district(region.getDistrict())
                .isActive(region.getIsActive())
                .build();
    }
}