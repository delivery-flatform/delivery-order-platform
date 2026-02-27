package com.delivery.project.category.service;

import com.delivery.project.category.dto.CategoryRequestDto;
import com.delivery.project.category.entity.Category;
import com.delivery.project.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        Category category = Category.builder()
                .name(requestDto.getName())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .createdBy("MANAGER")
                .build();

        categoryRepository.save(category);
    }

    // TODO: 카테고리 수정 (MANAGER+)
    // TODO: 카테고리 삭제 Soft Delete (MANAGER+)
}
