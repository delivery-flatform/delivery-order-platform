package com.delivery.project.review.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReviewRequestDto {

    private UUID storeId;
    private UUID orderId;
    private String content;
    private short rating;
}
