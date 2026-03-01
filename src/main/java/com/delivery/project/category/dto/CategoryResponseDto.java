package com.delivery.project.category.dto;

import com.delivery.project.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CategoryResponseDto {
    UUID id;
    String name;
    boolean is_active;

    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .is_active(category.getIsActive())
                .build();
    }
}
