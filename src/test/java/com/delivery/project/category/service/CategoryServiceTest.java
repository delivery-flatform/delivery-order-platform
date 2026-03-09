package com.delivery.project.category.service;

import com.delivery.project.category.dto.request.CategoryRequestDto;
import com.delivery.project.category.dto.request.CategoryUpdateDto;
import com.delivery.project.category.dto.response.CategoryResponseDto;
import com.delivery.project.category.entity.Category;
import com.delivery.project.category.repository.CategoryRepository;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name("치킨")
                .isActive(true)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("카테고리 단건 조회 성공")
    void selectCategorySuccess() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        CategoryResponseDto result = categoryService.selectCategory(categoryId);

        assertThat(result.getName()).isEqualTo("치킨");
    }

    @Test
    @DisplayName("카테고리 단건 조회 실패 - 존재하지 않음")
    void selectCategoryFailNotFound() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.selectCategory(categoryId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("카테고리 목록 조회 성공")
    void selectCategoryList() {

        Pageable pageable = PageRequest.of(0,10);
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        Page<CategoryResponseDto> result =
                categoryService.selectCategoryList(1,10,"createdAt",false);

        assertThat(result.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("허용되지 않은 size 입력 시 기본값 10 사용")
    void sizeValidation() {

        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        Page<CategoryResponseDto> result =
                categoryService.selectCategoryList(1, 999, "createdAt", false);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("허용되지 않은 sort 입력 시 createdAt 사용")
    void sortValidation() {

        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(page);

        // default인 createdAt으로 정렬
        Page<CategoryResponseDto> result =
                categoryService.selectCategoryList(1, 10, "invalid", false);

        assertThat(result.getContent()).hasSize(1);
    }
    
    @Test
    @DisplayName("카테고리 등록 성공")
    void insertCategory() {

        CategoryRequestDto dto = new CategoryRequestDto("치킨");

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponseDto result =
                categoryService.insertCategory("admin", dto);

        assertThat(result.getName()).isEqualTo("치킨");
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory() {

        CategoryUpdateDto dto =
                new CategoryUpdateDto("피자", false);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        CategoryResponseDto result =
                categoryService.updateCategory(categoryId, dto, "admin");

        assertThat(result.getName()).isEqualTo("피자");
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory() {

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId,"admin");

        assertThat(category.getDeletedBy()).isEqualTo("admin");
        assertThat(category.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("삭제된 카테고리 조회 실패")
    void selectDeletedCategoryFail() {

        category.deleteCategory("admin");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        assertThatThrownBy(() ->
                categoryService.selectCategory(categoryId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CATEGORY_ALREADY_DELETED.getMessage());
    }
}