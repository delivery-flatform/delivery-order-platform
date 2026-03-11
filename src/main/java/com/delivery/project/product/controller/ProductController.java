package com.delivery.project.product.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.product.dto.request.ProductCreateRequest;
import com.delivery.project.product.dto.request.ProductUpdateRequest;
import com.delivery.project.product.dto.response.ProductResponse;
import com.delivery.project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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


    // TODO: GET    /api/v1/products          - 상품 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            Authentication authentication) {

        // 🔥 허용된 size만 통과
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Pageable validatedPageable = PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_MANAGER") ||
                                auth.getAuthority().equals("ROLE_MASTER") ||
                                auth.getAuthority().equals("ROLE_OWNER")
                );

        Page<ProductResponse> products =
                productService.getProducts(validatedPageable, isAdmin);

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // TODO: GET    /api/v1/products/{id}     - 상품 단건 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_MANAGER") ||
                                auth.getAuthority().equals("ROLE_MASTER") ||
                                auth.getAuthority().equals("ROLE_OWNER")
                );

        ProductResponse product =
                productService.getProduct(productId, isAdmin);

        return ResponseEntity.ok(ApiResponse.success("조회가 완료되었습니다.", product));

    }

    // TODO: POST   /api/v1/products          - 상품 등록
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createProduct(
            @RequestBody ProductCreateRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        UUID productId = productService.createProduct(request, username);
        return ResponseEntity.ok(ApiResponse.success(productId));
    }

    // TODO: PUT    /api/v1/products/{id}     - 상품 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable UUID productId,
            @RequestBody ProductUpdateRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        productService.updateProduct(productId, request, username);
        return ResponseEntity.ok(ApiResponse.success("상품 수정 완료", null));
    }

    // TODO: DELETE /api/v1/products/{id}     - 상품 삭제
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        String username = authentication.getName();

        productService.deleteProduct(productId, username);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제 완료", null));
    }

    // TODO: PATCH  /api/v1/products/{id}/hide - 상품 숨김
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @PatchMapping("/{productId}/hide")
    public ResponseEntity<ApiResponse<Void>> hieProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        String username = authentication.getName();

        productService.hideProduct(productId, username);
        return ResponseEntity.ok(ApiResponse.success("상품 숨김 완료.", null));
    }

    // TODO: PATCH  /api/v1/products/{id}/hide - 상품 숨김
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
    @PatchMapping("/{productId}/unhide")
    public ResponseEntity<ApiResponse<Void>> unhideProduct(
            @PathVariable UUID productId,
            Authentication authentication) {

        String username = authentication.getName();

        productService.unhideProduct(productId, username);
        return ResponseEntity.ok(ApiResponse.success("상품 숨김 해제.", null));
    }
}
