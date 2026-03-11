package com.delivery.project.store.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.store.dto.request.StoreCategoryRequestDto;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.store.dto.response.StoreRatingResponseDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.service.StoreService;
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
public class StoreController {

    private final StoreService storeService;

//     가게 목록 조회 리뷰 평균 평점과 함께, 가게명 검색
    @GetMapping
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
    public ResponseEntity<ApiResponse<StoreRatingResponseDto>> selectStore(
            @PathVariable UUID id
    ) {
        StoreRatingResponseDto store = storeService.selectStore(id);

        return ResponseEntity.ok(ApiResponse.success(store));
    }

    // 가게 등록
    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponseDto>> insertStore(@RequestBody StoreRequestDto requestDto,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        StoreResponseDto store = storeService.insertStore(requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(store));
    }

    // 가게 수정
    @PutMapping("/{id}")
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
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable UUID id,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        storeService.deleteStore(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("가게가 삭제되었습니다.", null));
    }

    // 카테고리 등록
    @PostMapping("/{storeId}/categories")
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
    public ResponseEntity<ApiResponse<Void>> removeCategories(
            @PathVariable UUID storeId,
            @RequestBody StoreCategoryRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        storeService.removeCategories(storeId, requestDto.getCategoryIds(), userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("카테고리가 삭제되었습니다.", null));
    }
}
