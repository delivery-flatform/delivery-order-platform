package com.delivery.project.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryUpdateDto {
    private String name;
    private Boolean isActive;
}
