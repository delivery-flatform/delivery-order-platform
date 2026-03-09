package com.delivery.project.product.service;

import com.delivery.project.product.dto.request.ProductCreateRequest;
import com.delivery.project.product.dto.request.ProductUpdateRequest;
import com.delivery.project.product.dto.response.ProductResponse;
import com.delivery.project.product.entity.Product;
import com.delivery.project.product.repository.ProductRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    // TODO: 상품 목록 조회
    // TODO: 상품 단건 조회
    // TODO: 상품 등록 (OWNER+)
    // TODO: 상품 수정 (OWNER 본인 or MANAGER+)
    // TODO: 상품 숨김 처리
    // TODO: 상품 삭제 Soft Delete

    // TODO: 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Pageable pageable, boolean isAdmin) {

        Page<Product> productPage;

        if (isAdmin) {
            productPage = productRepository.findByDeletedAtIsNull(pageable);
        } else {
            productPage = productRepository
                    .findByDeletedAtIsNullAndIsHiddenFalse(pageable);
        }

        return productPage.map(ProductResponse::from);
    }

    // TODO: 상품 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId, boolean isAdmin) {

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        if (!isAdmin && product.getIsHidden()) {
            throw new RuntimeException("숨김 처리된 상품입니다.");
        }

        return ProductResponse.from(product);
    }

    // 1. 상품 등록
    public UUID createProduct(ProductCreateRequest request, String userName) {

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("가게가 존재하지 않습니다."));

        Product product = Product.create(
                store,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                userName
        );

        productRepository.save(product);

        return product.getId();
    }

    // 2. 상품 수정
    public void updateProduct(UUID productId, ProductUpdateRequest request, String userName) {

        Product product = getActiveProduct(productId);

        product.update(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                userName
        );
    }

    // 3. 상품 삭제 (Soft Delete)
    public void deleteProduct(UUID productId, String userName) {

        Product product = getActiveProduct(productId);

        product.delete(userName);
    }

    // 4. 상품 숨김 처리
    public void hideProduct(UUID productId, String userName) {

        Product product = getActiveProduct(productId);

        product.hide(userName);
    }

    // 5. 상품 숨김 해제
    public void unhideProduct(UUID productId, String userName) {

        Product product = getActiveProduct(productId);

        product.unhide(userName);
    }

    private Product getActiveProduct(UUID productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
    }
}
