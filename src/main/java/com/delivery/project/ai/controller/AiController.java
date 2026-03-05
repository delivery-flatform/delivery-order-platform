package com.delivery.project.ai.controller;

import com.delivery.project.ai.dto.request.AiRequestDto;
import com.delivery.project.ai.dto.response.AiResponseDto;
import com.delivery.project.ai.service.AiServiceImpl;
import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiServiceImpl aiService;

    private final List<Integer> allowedSizes = List.of(10, 30, 50);

    // TODO: GET  /api/v1/ai/logs        - AI 로그 조회 (MANAGER+)
    @GetMapping("/logs")
    public ResponseEntity<Page<AiResponseDto>> aiSelect(
            @RequestParam(defaultValue = "1",name = "page") int page,
            @RequestParam(defaultValue = "10",name="size") int size,
            @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
            @RequestParam(defaultValue = "true",name="isAsc") boolean isAsc,
            @RequestParam(required = false, value = "search") String search
    ) {
        // 페이징 처리 로직
        int finalSize = allowedSizes.contains(size) ? size : 10;

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page - 1, finalSize, sort);

        Page<AiResponseDto> result = aiService.aiSelect(pageable, search);
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