package com.delivery.project.region.entity;

import com.delivery.project.region.dto.RegionRequestDto;
import com.delivery.project.region.dto.request.RegionUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_region")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String district;

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

    public static Region toEntity(RegionRequestDto requestDto, String username) {
        return Region.builder()
                .name(requestDto.getName())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
    }

    public void updateRegion(String username, RegionUpdateRequestDto requestDto) {
        if (requestDto.getName() != null && !requestDto.getName().isBlank()) {
            this.name = requestDto.getName();
        }

        if (requestDto.getCity() != null && !requestDto.getCity().isBlank()) {
            this.city = requestDto.getCity();
        }

        if (requestDto.getDistrict() != null && !requestDto.getDistrict().isBlank()) {
            this.district = requestDto.getDistrict();
        }

        this.updatedAt = LocalDateTime.now();
        this.updatedBy = username;
    }

    public void deleteRegion(String username) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = username;
    }
}
