package com.delivery.project.store.entity;

import com.delivery.project.region.entity.Region;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_username", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "min_order_price", nullable = false)
    private Integer minOrderPrice;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen;

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

    @OneToMany(mappedBy = "store")
    private List<StoreCategory> storeCategoryList = new ArrayList<>();

    public static Store create(User user, Region region, StoreRequestDto requestDto, String createdBy) {
            return Store.builder()
                    .user(user)
                    .region(region)
                    .name(requestDto.getStoreName())
                    .description(requestDto.getDescription())
                    .phone(requestDto.getPhone())
                    .address(requestDto.getAddress())
                    .minOrderPrice(requestDto.getMinOrderPrice())
                    .isOpen(true)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .build();
    }

    public void delete(String username) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = username;
    }

    public void update(StoreUpdateRequestDto requestDto, String username) {
        this.name = requestDto.getStoreName();
        this.description = requestDto.getDescription();
        this.phone = requestDto.getPhone();
        this.address = requestDto.getAddress();
        this.minOrderPrice = requestDto.getMinOrderPrice();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = username;
    }
}
