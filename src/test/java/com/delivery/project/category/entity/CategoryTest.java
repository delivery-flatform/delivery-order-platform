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
    @DisplayName("Category 생성")
    void createCategory() {

        CategoryRequestDto dto = new CategoryRequestDto("치킨");

        Category category = Category.toEntity(dto, "admin");

        assertThat(category.getName()).isEqualTo("치킨");
        assertThat(category.getIsActive()).isTrue();
        assertThat(category.getCreatedBy()).isEqualTo("admin");
        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Category 수정")
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
    @DisplayName("Category 삭제 (Soft Delete)")
    void deleteCategory() {

        Category category = createDefaultCategory();

        category.deleteCategory("admin");

        assertThat(category.getDeletedBy()).isEqualTo("admin");
        assertThat(category.getDeletedAt()).isNotNull();
    }
}