package com.delivery.project.category.controller;

import com.delivery.project.category.dto.CategoryRequestDto;
import com.delivery.project.category.dto.CategoryResponseDto;
import com.delivery.project.category.dto.CategoryUpdateDto;
import com.delivery.project.category.service.CategoryService;
import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
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
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponseDto>>> selectCategoryList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc
    ) {
        Page<CategoryResponseDto> categoryList = categoryService.selectCategoryList(page, size, sortBy, isAsc);

        return ResponseEntity.ok(ApiResponse.success(categoryList));
    }

    // 카테고리 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> selectCategory(@PathVariable UUID id) {
        CategoryResponseDto response = categoryService.selectCategory(id);

        return ResponseEntity.ok(ApiResponse.success("조회가 완료되었습니다.", response));
    }

    // 카테고리 등록
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> insertCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto response = categoryService.insertCategory(userDetails.getUsername(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id, @RequestBody CategoryUpdateDto requestDto) {
        CategoryResponseDto response = categoryService.updateCategory(id, requestDto, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("카테고리 수정 완료", response));
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
