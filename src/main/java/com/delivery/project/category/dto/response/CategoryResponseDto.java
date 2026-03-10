package com.delivery.project.category.dto.response;

import com.delivery.project.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CategoryResponseDto {
    UUID id;
    String name;
    boolean isActive;

    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .isActive(category.getIsActive())
                .build();
    }
}
