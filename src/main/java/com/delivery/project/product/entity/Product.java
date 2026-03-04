package com.delivery.project.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;

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

    //  생성 메서드
    public static Product create(
            UUID storeId,
            String name,
            String description,
            Integer price,
            String createdBy
    ) {
        Product product = new Product();
        product.storeId = storeId;
        product.name = name;
        product.description = description;
        product.price = price;
        product.isHidden = false;
        product.createdAt = LocalDateTime.now();
        product.createdBy = createdBy;
        return product;
    }

    //  수정
    public void update(String name, String description, Integer price, String updatedBy) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    //  삭제 (Soft Delete)
    public void delete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    //  숨김
    public void hide(String updatedBy) {
        this.isHidden = true;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    //  숨김 해제
    public void unhide(String updatedBy) {
        this.isHidden = false;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}
