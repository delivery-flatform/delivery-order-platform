package com.delivery.project.review.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.review.dto.request.ReviewRequestDto;
import com.delivery.project.review.dto.request.ReviewUpdateRequestDto;
import com.delivery.project.review.dto.response.ReviewResponseDto;
import com.delivery.project.review.dto.response.ReviewUpdateResponseDto;
import com.delivery.project.review.entity.Review;
import com.delivery.project.review.repository.ReviewRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import com.delivery.project.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    // TODO: 리뷰 목록 조회 (가게별) 평균 별점 계산
    @PreAuthorize("hasRole('OWNER')")
    public Page<ReviewResponseDto> selectReview(UUID id, Pageable pageable, String search, User user) {
        //user의 name값과 store의 owner_user_name값 일치하는지 확인
        String ownerUsername = storeRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.STORE_NOT_FOUND)).getOwnerUsername();

         if(!user.getUsername().equals(ownerUsername)){
             throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Page<Review> reviewList;

        if(search == null){
            reviewList = reviewRepository.findByStoreIdAndDeletedAtIsNull(id,pageable);
        }else{
            reviewList = reviewRepository.findByStoreIdAndDeletedAtIsNullAndContentContaining(id,search,pageable);
        }

        return reviewList.map(ReviewResponseDto::from);
    }

    // TODO: 리뷰 목록 조회 (유저별)
    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<ReviewResponseDto> selectReview(Pageable pageable, String search, User user) {

        Page<Review> reviewList;

        if(search == null){
            // reviewRepository.findByUserUserNameAndDeletedAtisNull(user, pageable);
            reviewList = reviewRepository.findByUserUsernameAndDeletedAtIsNull(user.getUsername(), pageable);
        }else{
            reviewList = reviewRepository.
                    findByUserUsernameAndDeletedAtIsNullAndContentContaining(user.getUsername(), search, pageable);
        }

        return reviewList.map(ReviewResponseDto::from);
    }

    // TODO: 리뷰 작성 (주문 완료 후, rating 1~5)
    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponseDto createReview(ReviewRequestDto dto, User user) {

        String status = "Completed".toUpperCase(Locale.ROOT);
        // 주문 완료 상태인지 확인
       Order order = orderRepository.findByIdAndStatus(dto.getOrderId(), status).orElseThrow(()->
               new CustomException(ErrorCode.REVIEW_NOT_COMPLETED_ORDER)
        );

        // 가게 정보 확인
       Store store = storeRepository.findById(dto.getStoreId()).orElseThrow(() ->
               new CustomException(ErrorCode.STORE_NOT_FOUND)
       );

       // 리뷰가 존재하는지 확인
        if(reviewRepository.existsByOrderId(dto.getOrderId())){
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

       // 유저와 시킨 사람의 정보가 일치하는지 확인
       if(!user.getUsername().equals(order.getCustomerUsername())){
         throw new CustomException(ErrorCode.FORBIDDEN);
       }

        Review review = Review.builder()
                .content(dto.getContent())
                .createdBy(user.getUsername())
                .createdAt(LocalDateTime.now())
                .rating(dto.getRating())
                .order(order)
                .store(store)
                .user(user)
                .build();

       Review saveReview = reviewRepository.save(review);

       return ReviewResponseDto.from(saveReview);
    }

    // TODO: 리뷰 수정 (본인만)
    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewUpdateResponseDto updateReview(UUID id, ReviewUpdateRequestDto dto, User user) {

        // 리뷰 존재 확인
        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        review.updateReview(dto.getContent(), dto.getRating(),user);

        return ReviewUpdateResponseDto.from(review);
    }

    // TODO: 리뷰 삭제 Soft Delete
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER','MASTER','MANAGER')")
    public void deleteReview(UUID id, User user) {

        // 리뷰 존재 확인
        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        review.deleteReview(user);

    }

    // TODO : 리뷰 평점 계산 (QueryDSL ? JPQL?)


}
