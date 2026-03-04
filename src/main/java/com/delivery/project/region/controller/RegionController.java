package com.delivery.project.region.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.region.dto.RegionRequestDto;
import com.delivery.project.region.dto.RegionResponseDto;
import com.delivery.project.region.service.RegionService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    // TODO: GET    /api/v1/regions       - 지역 목록 조회
    @GetMapping
    public ResponseEntity<Page<RegionResponseDto>> selectRegionList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc
    ) {
        Page<RegionResponseDto> regionList = regionService.selectRegionList(page, size, sortBy, isAsc);

        return ResponseEntity.ok(regionList);
    }

    // 지역 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponseDto>> selectRegion(@PathVariable UUID id) {
        RegionResponseDto response = regionService.selectRegion(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 지역 등록
    @PostMapping
    public ResponseEntity<ApiResponse<RegionResponseDto>> insertRegion(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RegionRequestDto requestDto) {
        RegionResponseDto response = regionService.insertRegion(userDetails.getUsername(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // TODO: PUT    /api/v1/regions/{id}  - 지역 수정

    // TODO: DELETE /api/v1/regions/{id}  - 지역 삭제

}
