package com.delivery.project.region.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.region.dto.request.RegionRequestDto;
import com.delivery.project.region.dto.request.RegionUpdateRequestDto;
import com.delivery.project.region.dto.response.RegionResponseDto;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    private Region region;
    private UUID regionId;

    @BeforeEach
    void setUp() {

        regionId = UUID.randomUUID();

        region = Region.builder()
                .id(regionId)
                .name("광화문")
                .city("서울특별시")
                .district("종로구")
                .isActive(true)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("지역 단건 조회 성공")
    void selectRegionSuccess() {

        // given
        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        // when
        RegionResponseDto result =
                regionService.selectRegion(regionId);

        // then
        assertThat(result.getName()).isEqualTo("광화문");
        assertThat(result.getCity()).isEqualTo("서울특별시");
        assertThat(result.getDistrict()).isEqualTo("종로구");

        verify(regionRepository).findById(regionId);
    }

    @Test
    @DisplayName("지역 단건 조회 실패 - 존재하지 않음")
    void selectRegionFailNotFound() {

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                regionService.selectRegion(regionId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REGION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지역 목록 조회 성공")
    void selectRegionList() {

        Pageable pageable = PageRequest.of(0,10);

        Page<Region> page =
                new PageImpl<>(List.of(region));

        when(regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        Page<RegionResponseDto> result =
                regionService.selectRegionList(1,10,"createdAt",false);

        assertThat(result.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("허용되지 않은 size 입력 시 기본값 10 사용")
    void sizeValidation() {

        Page<Region> page =
                new PageImpl<>(List.of(region));

        when(regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        Page<RegionResponseDto> result =
                regionService.selectRegionList(1,999,"createdAt",false);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("허용되지 않은 sort 입력 시 createdAt 사용")
    void sortValidation() {

        Page<Region> page =
                new PageImpl<>(List.of(region));

        when(regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        Page<RegionResponseDto> result =
                regionService.selectRegionList(1,10,"invalid",false);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("지역 등록 성공")
    void insertRegion() {

        // given
        RegionRequestDto dto =
                new RegionRequestDto("광화문", "서울특별시", "종로구");

        when(regionRepository.save(any(Region.class)))
                .thenReturn(region);

        // when
        RegionResponseDto result =
                regionService.insertRegion("admin", dto);

        // then
        assertThat(result.getName()).isEqualTo("광화문");
        assertThat(result.getCity()).isEqualTo("서울특별시");
        assertThat(result.getDistrict()).isEqualTo("종로구");

        verify(regionRepository).save(any(Region.class));
    }

    @Test
    @DisplayName("지역 수정 성공")
    void updateRegion() {

        // given
        RegionUpdateRequestDto dto =
                new RegionUpdateRequestDto("해운대", "부산광역시", "해운대구");

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        // when
        RegionResponseDto result =
                regionService.updateRegion("admin", dto, regionId);

        // then
        assertThat(result.getName()).isEqualTo("해운대");
        assertThat(result.getCity()).isEqualTo("부산광역시");
        assertThat(result.getDistrict()).isEqualTo("해운대구");

        verify(regionRepository).findById(regionId);
    }

    @Test
    @DisplayName("지역 상태 변경 성공")
    void updateRegionStatus() {

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        RegionResponseDto result =
                regionService.updateRegionStatus(regionId,false,"admin");

        verify(regionRepository).findById(regionId);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("지역 삭제 성공")
    void deleteRegion() {

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        regionService.deleteRegion("admin", regionId);

        assertThat(region.getDeletedBy()).isEqualTo("admin");
        assertThat(region.getDeletedAt()).isNotNull();

        verify(regionRepository).findById(regionId);
    }

    @Test
    @DisplayName("삭제된 지역 수정 실패")
    void updateDeletedRegionFail() {

        region.deleteRegion("admin");

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        assertThatThrownBy(() ->
                regionService.updateRegion("admin",
                        new RegionUpdateRequestDto("해운대", "부산광역시", "해운대구"),
                        regionId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REGION_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("삭제된 지역 상태 변경 실패")
    void updateDeletedRegionStatusFail() {

        region.deleteRegion("admin");

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        assertThatThrownBy(() ->
                regionService.updateRegionStatus(regionId, false, "admin"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REGION_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 지역 수정 실패")
    void updateRegionNotFoundFail() {

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                regionService.updateRegion(
                        "admin",
                        new RegionUpdateRequestDto("해운대", "부산광역시", "해운대구"),
                        regionId
                ))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REGION_NOT_FOUND.getMessage());
    }
}