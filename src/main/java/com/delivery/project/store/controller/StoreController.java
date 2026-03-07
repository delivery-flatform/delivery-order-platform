package com.delivery.project.store.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
