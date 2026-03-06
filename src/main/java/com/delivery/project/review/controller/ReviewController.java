package com.delivery.project.review.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.review.dto.request.ReviewRequestDto;
import com.delivery.project.review.dto.request.ReviewUpdateRequestDto;
import com.delivery.project.review.dto.response.ReviewResponseDto;
import com.delivery.project.review.dto.response.ReviewUpdateResponseDto;
import com.delivery.project.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private final List<Integer> allowedSizes = List.of(10, 30, 50);

    // TODO: GET    /api/v1/reviews?storeId={id} - 가게별 리뷰 목록 조회
    @GetMapping("/storeId={id}")
    public ResponseEntity<Page<ReviewResponseDto>>  selectReview(@PathVariable UUID id,
                                                                 @RequestParam(defaultValue = "1",name = "page") int page,
                                                                 @RequestParam(defaultValue = "10",name="size") int size,
                                                                 @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
                                                                 @RequestParam(defaultValue = "false",name="isAsc") boolean isAsc,
                                                                 @RequestParam(required = false, value = "search")
                                                                     String search,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){

        // 페이징 처리 로직
        int finalSize = allowedSizes.contains(size) ? size : 10;

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page - 1, finalSize, sort);

        Page<ReviewResponseDto> reviewList = reviewService.selectReview(id,pageable, search, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(reviewList).getData());
    }

    // TODO: GET    /api/v1/reviews - 유저의 리뷰 목록 조회 (유저 인증 객체 가져와야함)
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> selectReview(@RequestParam(defaultValue = "1",name = "page") int page,
                                                                @RequestParam(defaultValue = "10",name="size") int size,
                                                                @RequestParam(defaultValue = "createdAt",name="sortBy") String sortBy,
                                                                @RequestParam(defaultValue = "false",name="isAsc") boolean isAsc,
                                                                @RequestParam(required = false, value = "search")
                                                                    String search,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails){

        // 페이징 처리 로직
        int finalSize = allowedSizes.contains(size) ? size : 10;

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page - 1, finalSize, sort);


        Page<ReviewResponseDto> reviewList = reviewService.selectReview(pageable,search, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(reviewList).getData());
    }
    
    // TODO: POST   /api/v1/reviews              - 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails){

        ReviewResponseDto responseDto = reviewService.createReview(requestDto, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(responseDto).getData());

    }

    // TODO: PUT    /api/v1/reviews/{id}         - 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(@PathVariable UUID id, @Valid @RequestBody ReviewUpdateRequestDto dto,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        ReviewUpdateResponseDto responseDto = reviewService.updateReview(id,dto, userDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(responseDto).getData());
    }

    // TODO: DELETE /api/v1/reviews/{id}         - 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        reviewService.deleteReview(id,userDetails.getUser());

        return ResponseEntity.ok("삭제되었습니다.");
    }
}
