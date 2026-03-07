package com.delivery.project.store.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.service.StoreService;
import lombok.RequiredArgsConstructor;
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

    // TODO: GET    /api/v1/stores          - 가게 목록 조회
    // TODO: GET    /api/v1/stores/{id}     - 가게 단건 조회
    // TODO: POST   /api/v1/stores          - 가게 등록
    // TODO: PUT    /api/v1/stores/{id}     - 가게 수정
    // TODO: DELETE /api/v1/stores/{id}     - 가게 삭제
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
}
