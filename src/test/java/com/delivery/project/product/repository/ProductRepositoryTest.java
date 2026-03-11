package com.delivery.project.product.repository;

import com.delivery.project.product.entity.Product;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @Test
    void saveProduct() {

        Store store = storeRepository.save(
                Store.builder()
                        .name("테스트 가게")
                        .build()
        );

        Product product = Product.create(
                store,
                "피자",
                "맛있는 피자",
                18000,
                "admin"
        );

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findAllByDeletedAtIsNull() {

        Store store = storeRepository.save(
                Store.builder()
                        .name("테스트 가게")
                        .build()
        );

        Product product = Product.create(
                store,
                "피자",
                "설명",
                18000,
                "admin"
        );

        productRepository.save(product);

        Page<Product> result =
                productRepository.findByDeletedAtIsNull(PageRequest.of(0, 10));

        assertThat(result.getContent().size()).isEqualTo(1);
    }

    @Test
    void findByDeletedAtIsNullAndIsHiddenFalse() {

        Store store = storeRepository.save(
                Store.builder()
                        .name("테스트 가게")
                        .build()
        );

        Product product = Product.create(
                store,
                "치킨",
                "설명",
                20000,
                "admin"
        );

        productRepository.save(product);

        Page<Product> result =
                productRepository.findByDeletedAtIsNullAndIsHiddenFalse(
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

}
