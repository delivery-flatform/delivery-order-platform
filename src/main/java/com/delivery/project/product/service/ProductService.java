package com.delivery.project.product.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    // TODO: 상품 등록
    // TODO: 상품 수정
    // TODO: 상품 숨김 처리
    // TODO: 상품 숨김 해제
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

        Product product = getActiveProduct(productId);

        if (!isAdmin && product.getIsHidden()) {
            throw new CustomException(ErrorCode.PRODUCT_HIDDEN);
        }

        return ProductResponse.from(product);
    }

    // TODO: 상품 등록
    @PreAuthorize("hasAnyRole('OWNER')")
    public UUID createProduct(ProductCreateRequest request, String userName) {

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> {
                    return new CustomException(ErrorCode.STORE_NOT_FOUND);
                });

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

    // TODO: 상품 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    public void updateProduct(UUID productId, ProductUpdateRequest request, String userName) {

        Product product = getActiveProduct(productId);

        product.update(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                userName
        );
    }

    // TODO: 상품 삭제 Soft Delete
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    public void deleteProduct(UUID productId, String userName) {

        Product product = getActiveProduct(productId);

        product.delete(userName);
    }

    // TODO: 상품 숨김 처리
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    public void hideProduct(UUID productId, String userName) {

        Product product = getActiveProduct(productId);

        product.hide(userName);
    }

    // TODO: 상품 숨김 해제
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    public void unhideProduct(UUID productId, String userName) {

        Product product = getActiveProduct(productId);

        product.unhide(userName);
    }

    private Product getActiveProduct(UUID productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() ->{
        return new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
    });

    }
}
