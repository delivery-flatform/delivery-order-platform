package com.delivery.project.region.entity;

import com.delivery.project.region.dto.request.RegionRequestDto;
import com.delivery.project.region.dto.request.RegionUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegionTest {

    @Test
    @DisplayName("지역 생성")
    void toEntityTest() {

        // given
        RegionRequestDto dto =
                new RegionRequestDto("광화문","서울특별시","종로구");

        // when
        Region region = Region.toEntity(dto,"admin");

        // then
        assertThat(region.getName()).isEqualTo("광화문");
        assertThat(region.getCity()).isEqualTo("서울특별시");
        assertThat(region.getDistrict()).isEqualTo("종로구");
        assertThat(region.getCreatedBy()).isEqualTo("admin");
        assertThat(region.getIsActive()).isTrue();
        assertThat(region.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("지역 정보 수정")
    void updateRegionTest() {

        // given
        Region region = Region.builder()
                .name("광화문")
                .city("서울특별시")
                .district("종로구")
                .isActive(true)
                .createdBy("admin")
                .build();

        RegionUpdateRequestDto dto =
                new RegionUpdateRequestDto("해운대","부산광역시","해운대구");

        // when
        region.updateRegion("admin2", dto);

        // then
        assertThat(region.getName()).isEqualTo("해운대");
        assertThat(region.getCity()).isEqualTo("부산광역시");
        assertThat(region.getDistrict()).isEqualTo("해운대구");
        assertThat(region.getUpdatedBy()).isEqualTo("admin2");
        assertThat(region.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("지역 상태 변경")
    void updateStatusTest() {

        // given
        Region region = Region.builder()
                .name("광화문")
                .city("서울특별시")
                .district("종로구")
                .isActive(true)
                .createdBy("admin")
                .build();

        // when
        region.updateStatus("admin", false);

        // then
        assertThat(region.getIsActive()).isFalse();
        assertThat(region.getUpdatedBy()).isEqualTo("admin");
        assertThat(region.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("지역 삭제")
    void deleteRegionTest() {

        // given
        Region region = Region.builder()
                .name("광화문")
                .city("서울특별시")
                .district("종로구")
                .isActive(true)
                .createdBy("admin")
                .build();

        // when
        region.deleteRegion("admin");

        // then
        assertThat(region.getDeletedBy()).isEqualTo("admin");
        assertThat(region.getDeletedAt()).isNotNull();
    }
}