package com.delivery.project.category.repository;

import com.delivery.project.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .isActive(true)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("카테고리 저장 테스트")
    void saveCategory() {

        Category category = createCategory("치킨");

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("치킨");
    }

    @Test
    @DisplayName("삭제되지 않은 카테고리 조회")
    void findAllByDeletedAtIsNull() {

        Category category1 = createCategory("치킨");
        Category category2 = createCategory("피자");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Page<Category> result =
                categoryRepository.findAllByDeletedAtIsNull(PageRequest.of(0,10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("삭제된 카테고리는 조회되지 않는다")
    void deletedCategoryNotReturned() {

        Category category1 = createCategory("치킨");
        Category category2 = createCategory("피자");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        category2.deleteCategory("admin");

        Page<Category> result =
                categoryRepository.findAllByDeletedAtIsNull(PageRequest.of(0,10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("치킨");
    }
}