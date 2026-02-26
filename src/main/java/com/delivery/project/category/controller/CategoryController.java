package com.delivery.project.category.controller;

import com.delivery.project.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // TODO: GET    /api/v1/categories       - 카테고리 목록 조회
    // TODO: GET    /api/v1/categories/{id}  - 카테고리 단건 조회
    // TODO: POST   /api/v1/categories       - 카테고리 등록
    // TODO: PUT    /api/v1/categories/{id}  - 카테고리 수정
    // TODO: DELETE /api/v1/categories/{id}  - 카테고리 삭제
}
