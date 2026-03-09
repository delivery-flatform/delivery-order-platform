package com.delivery.project.store.dto.request;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class StoreCategoryRequestDto {
    private List<UUID> categoryIds;
}