package com.delivery.project.region.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegionUpdateRequestDto {
    private String name;  // 지역명     ex: 광화문
    private String city;  // 시/도      ex: 서울특별시
    private String district;  // 구/군  ex: 종로구
}
