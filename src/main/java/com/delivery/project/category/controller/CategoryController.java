package com.delivery.project.category.controller;

import com.delivery.project.category.dto.CategoryRequestDto;
import com.delivery.project.category.dto.CategoryResponseDto;
import com.delivery.project.category.dto.CategoryUpdateDto;
import com.delivery.project.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping
    public Page<CategoryResponseDto> selectCategoryList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc
    ) {
        return categoryService.selectCategoryList(page, size, sortBy, isAsc);
    }

    // 카테고리 단건 조회
    @GetMapping("/{id}")
    public CategoryResponseDto selectCategory(@PathVariable UUID id) {
        return categoryService.selectCategory(id);
    }

    // 카테고리 등록
    @PostMapping
    public void insertCategory(@RequestBody CategoryRequestDto requestDto) {
        categoryService.insertCategory(requestDto);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public void updateCategory(@PathVariable UUID id, @RequestBody CategoryUpdateDto requestDto) {
        categoryService.updateCategory(id, requestDto);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
