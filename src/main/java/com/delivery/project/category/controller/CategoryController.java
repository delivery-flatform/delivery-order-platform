package com.delivery.project.category.controller;

import com.delivery.project.category.dto.request.CategoryRequestDto;
import com.delivery.project.category.dto.response.CategoryResponseDto;
import com.delivery.project.category.dto.request.CategoryUpdateDto;
import com.delivery.project.category.service.CategoryService;
import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 API", description = "가게 카테고리 생성, 조회, 수정, 삭제 API")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping
    @Operation(
            summary = "카테고리 목록 조회",
            description = "등록된 카테고리 목록을 페이지 기반으로 조회합니다. 페이지 번호, 크기, 정렬 기준을 설정할 수 있습니다."
    )
    public ResponseEntity<ApiResponse<Page<CategoryResponseDto>>> selectCategoryList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc
    ) {
        Page<CategoryResponseDto> categoryList = categoryService.selectCategoryList(page, size, sortBy, isAsc);

        return ResponseEntity.ok(ApiResponse.success(categoryList));
    }

    // 카테고리 단건 조회
    @GetMapping("/{id}")
    @Operation(
            summary = "카테고리 단건 조회",
            description = "카테고리 ID(UUID)를 이용하여 특정 카테고리 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<CategoryResponseDto>> selectCategory(@PathVariable UUID id) {
        CategoryResponseDto response = categoryService.selectCategory(id);

        return ResponseEntity.ok(ApiResponse.success("조회가 완료되었습니다.", response));
    }

    // 카테고리 등록
    @PostMapping
    @Operation(
            summary = "카테고리 등록",
            description = "새로운 카테고리를 생성합니다."
    )
    public ResponseEntity<ApiResponse<CategoryResponseDto>> insertCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CategoryRequestDto requestDto
    ) {
        CategoryResponseDto response = categoryService.insertCategory(userDetails.getUsername(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    @Operation(
            summary = "카테고리 수정",
            description = "카테고리 ID를 기반으로 카테고리 정보를 수정합니다."
    )
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id,
            @RequestBody CategoryUpdateDto requestDto
    ) {
        CategoryResponseDto response = categoryService.updateCategory(id, requestDto, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("카테고리 수정 완료", response));
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    @Operation(
            summary = "카테고리 삭제",
            description = "카테고리 ID를 기반으로 카테고리를 삭제합니다."
    )
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id
    ) {
        categoryService.deleteCategory(id, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("카테고리 삭제 완료", null));
    }
}