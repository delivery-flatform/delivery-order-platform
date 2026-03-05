package com.delivery.project.category.service;

import com.delivery.project.category.dto.CategoryRequestDto;
import com.delivery.project.category.dto.CategoryResponseDto;
import com.delivery.project.category.dto.CategoryUpdateDto;
import com.delivery.project.category.entity.Category;
import com.delivery.project.category.repository.CategoryRepository;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.delivery.project.global.util.PageableUtil.createPageable;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 목록 조회
    public Page<CategoryResponseDto> selectCategoryList(int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = createPageable(page,size,sortBy,isAsc);
        Page<Category> categoryPage = categoryRepository.findAllByDeletedAtIsNull(pageable);

        log.info("카테고리 목록 조회 완료 totalElements={}", categoryPage.getTotalElements());

        return categoryPage.map(CategoryResponseDto::from);
    }

    // 카테고리 단건 조회 (MANAGER+)
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    public CategoryResponseDto selectCategory(UUID id) {
        Category category = findActiveCategory(id);

        log.info("카테고리 단건 조회 완료 categoryId={}, categoryName={}", id, category.getName());

        return CategoryResponseDto.from(category);
    }

    // 카테고리 등록 (MANAGER+)
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public CategoryResponseDto insertCategory(String username, CategoryRequestDto requestDto) {
        Category category = Category.toEntity(requestDto, username);
        Category savedCategory = categoryRepository.save(category);

        log.info("카테고리 등록 완료 categoryId={}, username={}", savedCategory.getId(), username);

        return CategoryResponseDto.from(savedCategory);
    }

    // 카테고리 수정 (MANAGER+)
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryUpdateDto requestDto, String username) {
        Category category = findActiveCategory(id);
        category.updateCategory(requestDto, username);

        log.info("카테고리 수정 완료 categoryId={}, username={}", id, username);
        return CategoryResponseDto.from(category);
    }

    // 카테고리 삭제 Soft Delete (MANAGER+)
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public void deleteCategory(UUID id, String username) {
        Category category = this.findCategory(id);
        category.deleteCategory(username);

        log.info("카테고리 삭제 완료 categoryId={}, username={}", id, username);
    }

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("카테고리 조회 실패 categoryId={}", id);
            
            return new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        });
    }

    private Category findActiveCategory(UUID id) {
        Category category = findCategory(id);

        if (category.getDeletedAt() != null) {
            log.warn("삭제된 카테고리 접근 시도 categoryId={}", id);
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_DELETED);
        }

        return category;
    }
}
