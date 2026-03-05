package com.delivery.project.ai.controller;

import com.delivery.project.ai.dto.request.AiRequestDto;
import com.delivery.project.ai.dto.response.AiResponseDto;
import com.delivery.project.ai.service.AiServiceImpl;
import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiServiceImpl aiService;

    // TODO: GET  /api/v1/ai/logs        - AI 로그 조회 (MANAGER+)
    @GetMapping("/logs")
    public ResponseEntity<Page<AiResponseDto>> aiSelect(
            @RequestParam(defaultValue = "1",name = "page") int page,
            @RequestParam(defaultValue = "10",name="size") int size,
            @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
            @RequestParam(defaultValue = "true",name="isAsc") boolean isAsc,
            @RequestParam(required = false, value = "search") String search
    ) {
        Page<AiResponseDto> result = aiService.aiSelect(
                page - 1, size, sortBy, isAsc, search);
        return ResponseEntity.ok(ApiResponse.success(result).getData());
    }

    // TODO: POST /api/v1/ai/contents - 상품 설명 생성 (50자 이하)
    @PostMapping("/contents")
    public ResponseEntity<ApiResponse<String>> aiInsert(@Valid @RequestBody AiRequestDto dto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String result = aiService.aiInsert(dto,userDetails.getUser().getUsername());

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}