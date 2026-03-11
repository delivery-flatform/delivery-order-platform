package com.delivery.project.region.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.region.dto.request.RegionRequestDto;
import com.delivery.project.region.dto.response.RegionResponseDto;
import com.delivery.project.region.dto.request.RegionUpdateRequestDto;
import com.delivery.project.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "지역 API", description = "지역 생성, 조회, 수정, 삭제 및 상태 변경을 관리하는 API")
public class RegionController {

    private final RegionService regionService;

    // 지역 목록 조회
    @GetMapping
    @Operation(
            summary = "지역 목록 조회",
            description = "등록된 지역 목록을 페이지 기반으로 조회합니다. 페이지 번호, 크기, 정렬 기준을 설정할 수 있습니다."
    )
    public ResponseEntity<ApiResponse<Page<RegionResponseDto>>> selectRegionList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc
    ) {
        Page<RegionResponseDto> regionList = regionService.selectRegionList(page, size, sortBy, isAsc);

        return ResponseEntity.ok(ApiResponse.success(regionList));
    }

    // 지역 단건 조회
    @GetMapping("/{id}")
    @Operation(
            summary = "지역 단건 조회",
            description = "지역 ID(UUID)를 이용하여 특정 지역 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<RegionResponseDto>> selectRegion(@PathVariable UUID id) {
        RegionResponseDto response = regionService.selectRegion(id);

        return ResponseEntity.ok(ApiResponse.success("조회가 완료되었습니다.", response));
    }

    // 지역 등록
    @PostMapping
    @Operation(
            summary = "지역 등록",
            description = "새로운 지역을 생성합니다. 지역명, 시/도, 구/군을 입력받습니다."
    )
    public ResponseEntity<ApiResponse<RegionResponseDto>> insertRegion(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RegionRequestDto requestDto) {
        RegionResponseDto response = regionService.insertRegion(userDetails.getUsername(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 지역 수정
    @PutMapping("/{id}")
    @Operation(
            summary = "지역 수정",
            description = "기존 지역 정보를 수정합니다. 지역 ID를 기반으로 수정이 진행됩니다."
    )
    public ResponseEntity<ApiResponse<RegionResponseDto>> updateRegion(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestBody RegionUpdateRequestDto requestDto,
                                                                       @PathVariable UUID id) {
        RegionResponseDto response = regionService.updateRegion(userDetails.getUsername(), requestDto, id);

        return ResponseEntity.ok(ApiResponse.success("지역 수정이 완료되었습니다.", response));
    }

    // 지역 상태 변경
    @PutMapping("/{id}/status")
    @Operation(
            summary = "지역 활성 상태 변경",
            description = "지역의 활성/비활성 상태(isActive)를 변경합니다."
    )
    public ResponseEntity<ApiResponse<RegionResponseDto>> updateRegionStatus(
            @PathVariable UUID id,
            @RequestParam boolean isActive,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        RegionResponseDto response = regionService.updateRegionStatus(id, isActive, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("지역 상태 변경되었습니다.", response));
    }

    // 지역 삭제
    @DeleteMapping("/{id}")
    @Operation(
            summary = "지역 삭제",
            description = "지역 ID를 기반으로 지역을 삭제합니다."
    )
    public ResponseEntity<ApiResponse<Void>> deleteRegion(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        regionService.deleteRegion(userDetails.getUsername(), id);

        return ResponseEntity.ok(ApiResponse.success("지역 삭제가 완료되었습니다.", null));
    }
}
