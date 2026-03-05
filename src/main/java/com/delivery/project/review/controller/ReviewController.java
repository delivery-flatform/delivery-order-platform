package com.delivery.project.review.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.global.util.PageableUtil;
import com.delivery.project.review.dto.request.ReviewRequestDto;
import com.delivery.project.review.dto.response.ReviewResponseDto;
import com.delivery.project.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // TODO: GET    /api/v1/reviews?storeId={id} - 가게별 리뷰 목록 조회
    @GetMapping("/storeId={id}")
    public ResponseEntity<Page<ReviewResponseDto>>  selectReview(@PathVariable UUID id,
                                                                 @RequestParam(defaultValue = "1",name = "page") int page,
                                                                 @RequestParam(defaultValue = "10",name="size") int size,
                                                                 @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
                                                                 @RequestParam(defaultValue = "true",name="isAsc") boolean isAsc,
                                                                 @RequestParam(required = false, value = "search")
                                                                     String search,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){

        Pageable pageable = PageableUtil.createPageable(page-1,size,sortBy,isAsc);

        Page<ReviewResponseDto> reviewList = reviewService.selectReview(id,pageable, search, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(reviewList).getData());
    }

    // TODO: GET    /api/v1/reviews - 유저의 리뷰 목록 조회 (유저 인증 객체 가져와야함)
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> selectReview(@RequestParam(defaultValue = "1",name = "page") int page,
                                                                @RequestParam(defaultValue = "10",name="size") int size,
                                                                @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
                                                                @RequestParam(defaultValue = "true",name="isAsc") boolean isAsc,
                                                                @RequestParam(required = false, value = "search")
                                                                    String search, UserDetailsImpl userDetails){
        Page<ReviewResponseDto> reviewList = reviewService.selectReview(page-1,size,sortBy, isAsc,search, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(reviewList).getData());
    }
    
    // TODO: POST   /api/v1/reviews              - 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto requestDto,
                                                          @RequestParam("orderId") UUID orderId,
                                                          @RequestParam("storeId") UUID storeId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails){

        ReviewResponseDto responseDto = reviewService.createReview(requestDto, orderId, storeId, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(responseDto).getData());

    }

    // TODO: PUT    /api/v1/reviews/{id}         - 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable UUID id, @RequestBody ReviewRequestDto dto,
                                                          UserDetailsImpl userDetails){
        ReviewResponseDto responseDto = reviewService.updateReview(id,dto, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(responseDto).getData());
    }

    // TODO: DELETE /api/v1/reviews/{id}         - 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> deleteReview(@PathVariable UUID id, UserDetailsImpl userDetails){
        ReviewResponseDto responseDto = reviewService.deleteReview(id,userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(responseDto).getData());
    }
}
