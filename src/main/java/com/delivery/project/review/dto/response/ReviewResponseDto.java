package com.delivery.project.review.dto.response;

import com.delivery.project.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    private UUID storeId;
    private UUID orderId;
    private String userName;
    private short rating;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public ReviewResponseDto(Review review){
        this.storeId = review.getStore().getId();
        this.orderId = review.getOrder().getId();
        this.userName = review.getUser().getNickname();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createAt = review.getCreatedAt();
    }
}
