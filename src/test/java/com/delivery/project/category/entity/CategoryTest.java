package com.delivery.project.category.entity;

import com.delivery.project.category.dto.request.CategoryRequestDto;
import com.delivery.project.category.dto.request.CategoryUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    private Category createDefaultCategory() {
        return Category.builder()
                .name("치킨")
                .isActive(true)
                .createdBy("admin")
                .build();
    }

    @Test
    @DisplayName("Category 생성 테스트")
    void createCategory() {

        CategoryRequestDto dto = new CategoryRequestDto("치킨");

        Category category = Category.toEntity(dto, "admin");

        assertThat(category.getName()).isEqualTo("치킨");
        assertThat(category.getIsActive()).isTrue();
        assertThat(category.getCreatedBy()).isEqualTo("admin");
        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 생성 시 활성 상태는 true")
    void createCategoryIsActiveTrue() {

        CategoryRequestDto dto = new CategoryRequestDto("피자");

        Category category = Category.toEntity(dto, "admin");

        assertThat(category.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Category 생성 시 createdBy 저장")
    void createCategoryCreatedBy() {

        CategoryRequestDto dto = new CategoryRequestDto("분식");

        Category category = Category.toEntity(dto, "manager");

        assertThat(category.getCreatedBy()).isEqualTo("manager");
    }

    @Test
    @DisplayName("Category 수정 테스트")
    void updateCategory() {

        Category category = createDefaultCategory();

        CategoryUpdateDto dto = new CategoryUpdateDto("피자", false);

        category.updateCategory(dto, "manager");

        assertThat(category.getName()).isEqualTo("피자");
        assertThat(category.getIsActive()).isFalse();
        assertThat(category.getUpdatedBy()).isEqualTo("manager");
        assertThat(category.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 수정 시 updatedBy 저장")
    void updateCategoryUpdatedBy() {

        Category category = createDefaultCategory();

        CategoryUpdateDto dto = new CategoryUpdateDto("한식", true);

        category.updateCategory(dto, "master");

        assertThat(category.getUpdatedBy()).isEqualTo("master");
    }

    @Test
    @DisplayName("Category 수정 시 updatedAt 생성")
    void updateCategoryUpdatedAt() {

        Category category = createDefaultCategory();

        CategoryUpdateDto dto = new CategoryUpdateDto("일식", true);

        category.updateCategory(dto, "admin");

        assertThat(category.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 삭제 테스트")
    void deleteCategory() {

        Category category = createDefaultCategory();

        category.deleteCategory("admin");

        assertThat(category.getDeletedBy()).isEqualTo("admin");
        assertThat(category.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 삭제 시 deletedAt 생성")
    void deleteCategoryDeletedAt() {

        Category category = createDefaultCategory();

        category.deleteCategory("manager");

        assertThat(category.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 삭제 시 deletedBy 저장")
    void deleteCategoryDeletedBy() {

        Category category = createDefaultCategory();

        category.deleteCategory("master");

        assertThat(category.getDeletedBy()).isEqualTo("master");
    }
}