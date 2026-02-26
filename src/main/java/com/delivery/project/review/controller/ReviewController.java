package com.delivery.project.review.controller;

import com.delivery.project.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // TODO: GET    /api/v1/reviews?storeId={id} - 리뷰 목록 조회
    // TODO: POST   /api/v1/reviews              - 리뷰 작성
    // TODO: PUT    /api/v1/reviews/{id}         - 리뷰 수정
    // TODO: DELETE /api/v1/reviews/{id}         - 리뷰 삭제
}
