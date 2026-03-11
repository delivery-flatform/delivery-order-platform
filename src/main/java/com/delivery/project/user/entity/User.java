package com.delivery.project.user.entity;

import com.delivery.project.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "username", length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

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

    @OneToMany(mappedBy = "user")
    private List<Store> storeList = new ArrayList<>();

    public void update(String nickname, String encodedPassword, String email, Boolean isPublic, String updatedBy) {
        Optional.ofNullable(nickname).ifPresent(v -> this.nickname = v);
        Optional.ofNullable(encodedPassword).ifPresent(v -> this.password = v);
        Optional.ofNullable(email).ifPresent(v -> this.email = v);
        Optional.ofNullable(isPublic).ifPresent(v -> this.isPublic = v);
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void softDelete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }
}
