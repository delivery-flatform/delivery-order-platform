package com.delivery.project.product.entity;

import com.delivery.project.store.entity.Store;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ProductTest {

    @Test
    void createProduct() {

        Store store = mock(Store.class);

        String name = "치킨";
        String description = "맛있는 치킨";
        Integer price = 20000;
        String createdBy = "admin";

        Product product = Product.create(
                store,
                name,
                description,
                price,
                createdBy
        );

        assertThat(product.getStore()).isEqualTo(store);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getIsHidden()).isFalse();
        assertThat(product.getCreatedBy()).isEqualTo(createdBy);
    }

    @Test
    void updateProduct() {

        Store store = mock(Store.class);

        Product product = Product.create(
                store,
                "치킨",
                "치킨 설명",
                20000,
                "admin"
        );

        product.update("양념치킨", "양념", 22000, "admin2");

        assertThat(product.getName()).isEqualTo("양념치킨");
        assertThat(product.getDescription()).isEqualTo("양념");
        assertThat(product.getPrice()).isEqualTo(22000);
        assertThat(product.getUpdatedBy()).isEqualTo("admin2");
    }

    @Test
    void hideProduct() {

        Store store = mock(Store.class);

        Product product = Product.create(
                store,
                "치킨",
                "설명",
                20000,
                "admin"
        );

        product.hide("admin");

        assertThat(product.getIsHidden()).isTrue();
    }

}