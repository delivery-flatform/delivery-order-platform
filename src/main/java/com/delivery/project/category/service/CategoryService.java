package com.delivery.project.category.service;

import com.delivery.project.category.dto.CategoryRequestDto;
import com.delivery.project.category.dto.CategoryUpdateDto;
import com.delivery.project.category.entity.Category;
import com.delivery.project.category.repository.CategoryRepository;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // TODO: 카테고리 목록 조회
    // TODO: 카테고리 단건 조회

    // TODO: 카테고리 등록 (MANAGER+)
    @Transactional
    public void insertCategory(CategoryRequestDto requestDto) {
        // TODO: MANAGER 권한 이상만 추가 가능
        Category category = Category.builder()
                .name(requestDto.getName())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .createdBy("MANAGER")
                .build();

        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategory(UUID id, CategoryUpdateDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // TODO: updatedBy 추가
        category.updateCategory(requestDto);
    }

    // TODO: 카테고리 수정 (MANAGER+)

    // TODO: 카테고리 삭제 Soft Delete (MANAGER+)
}
