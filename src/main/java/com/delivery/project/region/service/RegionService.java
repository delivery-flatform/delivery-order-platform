package com.delivery.project.region.service;

import com.delivery.project.region.dto.RegionRequestDto;
import com.delivery.project.region.dto.RegionResponseDto;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;

    // TODO: 지역 목록 조회
    // TODO: 지역 단건 조회

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
}
