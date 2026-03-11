package com.delivery.project.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewUpdateRequestDto {

    private String content;
    private short rating;
}
