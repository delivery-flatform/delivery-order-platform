package com.delivery.project.store.entity;

import com.delivery.project.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StoreCategoryTest {

    @Test
    @DisplayName("StoreCategory 생성 테스트")
    void createStoreCategory() {

        Store store = mock(Store.class);
        Category category = mock(Category.class);

        StoreCategory storeCategory = StoreCategory.create(store, category);

        assertThat(storeCategory.getStore()).isEqualTo(store);
        assertThat(storeCategory.getCategory()).isEqualTo(category);
    }
}