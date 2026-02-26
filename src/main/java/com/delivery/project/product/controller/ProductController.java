package com.delivery.project.product.controller;

import com.delivery.project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
