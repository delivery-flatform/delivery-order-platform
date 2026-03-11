package com.delivery.project.review.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "주문 번호(orderId)는 필수입니다.")
    private UUID orderId;

    private String content;
    private short rating;
}
