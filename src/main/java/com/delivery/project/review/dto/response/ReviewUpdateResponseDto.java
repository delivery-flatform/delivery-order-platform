package com.delivery.project.review.dto.response;

import com.delivery.project.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReviewUpdateResponseDto {

    private String content;
    private Short rating;
    private LocalDateTime updatedAt;

    public static ReviewUpdateResponseDto from(Review review){
        return ReviewUpdateResponseDto.builder()
                .content(review.getContent())
                .rating(review.getRating())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
