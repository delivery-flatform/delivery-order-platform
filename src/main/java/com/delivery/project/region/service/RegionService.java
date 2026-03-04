package com.delivery.project.region.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.region.dto.RegionRequestDto;
import com.delivery.project.region.dto.RegionResponseDto;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;
    private static final List<Integer> ALLOWED_SIZES = List.of(10, 30, 50);
    private static final List<String> ALLOWED_SORTS = List.of("createdAt", "name");


    // TODO: 지역 목록 조회(지역별 필터링은 나중에 추가)
    @Transactional(readOnly = true)
    public Page<RegionResponseDto> selectRegionList(int page, int size, String sortBy, boolean isAsc) {
        if (!ALLOWED_SORTS.contains(sortBy)) {
            sortBy = "createdAt";
        }
        if (!ALLOWED_SIZES.contains(size)) {
            size = 10;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Region> regionPage = regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(pageable);

        return regionPage.map(RegionResponseDto::from);
    }

    // 지역 단건 조회

    @Transactional(readOnly = true)
    public RegionResponseDto selectRegion(UUID id) {
        Region region = findRegion(id);

        return RegionResponseDto.from(region);
    }

    // 지역 등록 (MANAGER+)

    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public RegionResponseDto insertRegion(String username, RegionRequestDto requestDto) {
        Region region = Region.toEntity(requestDto, username);
        Region savedRegion = regionRepository.save(region);

        return RegionResponseDto.from(savedRegion);
    }

    // TODO: 지역 수정 (MANAGER+)

    // TODO: 지역 삭제 Soft Delete (MANAGER+)

    private Region findRegion(UUID id) {
        return regionRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));
    }
}
