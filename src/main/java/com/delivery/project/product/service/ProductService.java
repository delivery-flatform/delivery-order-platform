package com.delivery.project.product.service;

import com.delivery.project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // TODO: 상품 목록 조회
    // TODO: 상품 단건 조회
    // TODO: 상품 등록 (OWNER+)
    // TODO: 상품 수정 (OWNER 본인 or MANAGER+)
    // TODO: 상품 숨김 처리
    // TODO: 상품 삭제 Soft Delete
}
