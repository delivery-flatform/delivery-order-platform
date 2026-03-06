package com.delivery.project.review.dto.response;

import com.delivery.project.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private UUID storeId;
    private UUID orderId;
    private String userName;
    private short rating;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review){
        return ReviewResponseDto.builder()
                .storeId(review.getStore().getId())
                .orderId(review.getOrder().getId())
                .userName(review.getUser().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
