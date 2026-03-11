package com.delivery.project.store.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.store.dto.request.StoreCategoryRequestDto;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.store.dto.response.StoreRatingResponseDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.service.StoreService;
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
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "가게 API", description = "가게 조회, 등록, 수정, 삭제 및 카테고리 관리 API")
public class StoreController {

    private final StoreService storeService;

    // 가게 목록 조회 리뷰 평균 평점과 함께, 가게명 검색
    @GetMapping
    @Operation(
            summary = "가게 목록 조회",
            description = "가게 목록을 페이지 기반으로 조회합니다. 리뷰 평균 평점이 함께 반환되며 가게 이름(keyword)으로 검색할 수 있습니다."
    )
    public ResponseEntity<ApiResponse<Page<StoreRatingResponseDto>>> selectStoreList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc,
            @RequestParam(required = false) String keyword
    ) {
        Page<StoreRatingResponseDto> stores = storeService.selectStoreList(page, size, sortBy, isAsc, keyword);
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    // 가게 단건 조회 리뷰 평균 평점과 함께
    @GetMapping("/{id}")
    @Operation(
            summary = "가게 단건 조회",
            description = "가게 ID(UUID)를 이용하여 특정 가게 정보를 조회합니다. 리뷰 평균 평점 정보가 함께 반환됩니다."
    )
    public ResponseEntity<ApiResponse<StoreRatingResponseDto>> selectStore(
            @PathVariable UUID id
    ) {
        StoreRatingResponseDto store = storeService.selectStore(id);

        return ResponseEntity.ok(ApiResponse.success(store));
    }

    // 가게 등록
    @PostMapping
    @Operation(
            summary = "가게 등록",
            description = "새로운 가게를 등록합니다."
    )
    public ResponseEntity<ApiResponse<StoreResponseDto>> insertStore(
            @RequestBody StoreRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        StoreResponseDto store = storeService.insertStore(requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(store));
    }

    // 가게 수정
    @PutMapping("/{id}")
    @Operation(
            summary = "가게 수정",
            description = "가게 ID를 기반으로 가게 정보를 수정합니다."
    )
    public ResponseEntity<ApiResponse<StoreResponseDto>> updateStore(
            @PathVariable UUID id,
            @RequestBody StoreUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        StoreResponseDto store = storeService.updateStore(id, requestDto, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("가게가 수정되었습니다.", store));
    }

    // 가게 영업상태 변경
    @PutMapping("/{id}/status")
    @Operation(
            summary = "가게 영업 상태 변경",
            description = "가게의 영업 상태(isOpen)를 변경합니다."
    )
    public ResponseEntity<ApiResponse<StoreResponseDto>> updateStoreStatus(
            @PathVariable UUID id,
            @RequestParam boolean isOpen,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        StoreResponseDto response = storeService.updateStoreStatus(id, isOpen, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("가게 상태 변경되었습니다.", response));
    }

    // 가게 삭제
    @DeleteMapping("/{id}")
    @Operation(
            summary = "가게 삭제",
            description = "가게 ID를 기반으로 가게를 삭제합니다."
    )
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        storeService.deleteStore(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("가게가 삭제되었습니다.", null));
    }

    // 카테고리 등록
    @PostMapping("/{storeId}/categories")
    @Operation(
            summary = "가게 카테고리 추가",
            description = "특정 가게에 카테고리를 추가합니다. 추가할 카테고리 id를 리스트로 전달해야 합니다."
    )
    public ResponseEntity<ApiResponse<Void>> addCategory(
            @PathVariable UUID storeId,
            @RequestBody StoreCategoryRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        storeService.addCategories(storeId, requestDto.getCategoryIds(), userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("카테고리가 추가되었습니다.", null));
    }

    // 카테고리 삭제
    @DeleteMapping("/{storeId}/categories")
    @Operation(
            summary = "가게 카테고리 삭제",
            description = "특정 가게에 등록된 카테고리를 삭제합니다. 추가할 카테고리 id를 리스트로 전달해야 합니다."
    )
    public ResponseEntity<ApiResponse<Void>> removeCategories(
            @PathVariable UUID storeId,
            @RequestBody StoreCategoryRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        storeService.removeCategories(storeId, requestDto.getCategoryIds(), userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("카테고리가 삭제되었습니다.", null));
    }
}