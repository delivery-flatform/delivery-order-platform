package com.delivery.project.region.dto;

import com.delivery.project.region.entity.Region;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResponseDto {

    private String id;
    private String name;
    private String city;
    private String district;

    public static RegionResponseDto from(Region region) {
        return RegionResponseDto.builder()
                .id(region.getId().toString())
                .name(region.getName())
                .city(region.getCity())
                .district(region.getDistrict())
                .build();
    }
}