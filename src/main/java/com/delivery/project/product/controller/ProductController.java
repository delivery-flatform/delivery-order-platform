package com.delivery.project.product.controller;

import com.delivery.project.product.dto.request.ProductCreateRequest;
import com.delivery.project.product.dto.request.ProductUpdateRequest;
import com.delivery.project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // TODO: GET    /api/v1/products          - 상품 목록 조회
    // TODO: GET    /api/v1/products/{id}     - 상품 단건 조회
    // TODO: POST   /api/v1/products          - 상품 등록
    // TODO: PUT    /api/v1/products/{id}     - 상품 수정
    // TODO: PATCH  /api/v1/products/{id}/hide - 상품 숨김
    // TODO: DELETE /api/v1/products/{id}     - 상품 삭제

    // TODO: POST   /api/v1/products          - 상품 등록
    @PostMapping
    public ResponseEntity<UUID> createProduct(
            @RequestBody ProductCreateRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        UUID productId = productService.createProduct(request, username);
        return ResponseEntity.ok(productId);
    }

    // TODO: PUT    /api/v1/products/{id}     - 상품 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable UUID productId,
            @RequestBody ProductUpdateRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        productService.updateProduct(productId, request, username);
        return ResponseEntity.ok().build();
    }

    // TODO: DELETE /api/v1/products/{id}     - 상품 삭제
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        String username = authentication.getName();

        productService.deleteProduct(productId, username);
        return ResponseEntity.ok().build();
    }

    // TODO: PATCH  /api/v1/products/{id}/hide - 상품 숨김
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @PatchMapping("/{productId}/hide")
    public ResponseEntity<Void> hideProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        String username = authentication.getName();

        productService.hideProduct(productId, username);
        return ResponseEntity.ok().build();
    }

    // TODO: PATCH  /api/v1/products/{id}/hide - 상품 숨김
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @PatchMapping("/{productId}/unhide")
    public ResponseEntity<Void> unhideProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        String username = authentication.getName();

        productService.unhideProduct(productId, username);
        return ResponseEntity.ok().build();
    }
}
