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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private static final List<Integer> ALLOWED_SIZES = List.of(10, 30, 50);
    private static final List<String> ALLOWED_SORTS = List.of("createdAt", "name");

    // 카테고리 목록 조회
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> selectCategoryList(int page, int size, String sortBy, boolean isAsc) {
        if (!ALLOWED_SORTS.contains(sortBy)) {
            sortBy = "createdAt";
        }
        if (!ALLOWED_SIZES.contains(size)) {
            size = 10;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoryPage = categoryRepository.findAllByDeletedAtIsNull(pageable);

        return categoryPage.map(CategoryResponseDto::from);
    }

    // 카테고리 단건 조회 (MANAGER+)
//    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional(readOnly = true)
    public CategoryResponseDto selectCategory(UUID id) {
        Category category = this.findCategory(id);

        return CategoryResponseDto.from(category);
    }

    // 카테고리 등록 (MANAGER+
//    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
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

    // 카테고리 수정 (MANAGER+)
//    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public void updateCategory(UUID id, CategoryUpdateDto requestDto) {
        Category category = this.findCategory(id);

        // TODO: updatedBy 추가
        category.updateCategory(requestDto);
    }

    // 카테고리 삭제 Soft Delete (MANAGER+)
//    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = this.findCategory(id);

        category.deleteCategory("MANAGER"); // username 넣기
    }

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
