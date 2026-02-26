package com.delivery.project.review.service;

import com.delivery.project.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // TODO: 리뷰 목록 조회 (가게별)
    // TODO: 리뷰 작성 (주문 완료 후, rating 1~5)
    // TODO: 리뷰 수정 (본인만)
    // TODO: 리뷰 삭제 Soft Delete
}
