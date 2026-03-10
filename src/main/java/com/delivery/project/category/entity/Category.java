package com.delivery.project.category.entity;

import com.delivery.project.category.dto.request.CategoryRequestDto;
import com.delivery.project.category.dto.request.CategoryUpdateDto;
import com.delivery.project.store.entity.StoreCategory;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @OneToMany(mappedBy = "category")
    private List<StoreCategory> storeCategoryList = new ArrayList<>();

    public static Category toEntity(CategoryRequestDto requestDto, String username) {
        return Category.builder()
                .name(requestDto.getName())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
    }

    public void updateCategory(CategoryUpdateDto requestDto, String updatedBy) {
        this.name = requestDto.getName();
        this.isActive = requestDto.getIsActive();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void deleteCategory(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
