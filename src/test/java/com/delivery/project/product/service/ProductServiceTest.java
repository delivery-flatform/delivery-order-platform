package com.delivery.project.product.service;

import com.delivery.project.product.dto.request.ProductCreateRequest;
import com.delivery.project.product.entity.Product;
import com.delivery.project.product.repository.ProductRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    StoreRepository storeRepository;

    @InjectMocks
    ProductService productService;

    @Test
    void insertProduct() {

        UUID storeId = UUID.randomUUID();

        Store store = mock(Store.class);

        ProductCreateRequest request =
                new ProductCreateRequest(storeId, "치킨", "설명", 20000);

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        Product savedProduct = Product.create(
                store,
                "치킨",
                "설명",
                20000,
                "admin"
        );

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        UUID result = productService.createProduct(request, "admin");

        assertThat(result).isNotNull();
    }


    @Test
    void hideProduct() {

        UUID productId = UUID.randomUUID();

        Store store = mock(Store.class);

        Product product = Product.create(
                store,
                "치킨",
                "설명",
                20000,
                "admin"
        );

        when(productRepository.findByIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        productService.hideProduct(productId, "admin");

        assertThat(product.getIsHidden()).isTrue();
    }
}

