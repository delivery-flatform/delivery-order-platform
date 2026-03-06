package com.delivery.project.review.entity;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.order.entity.Order;
import com.delivery.project.store.entity.Store;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="username", nullable = false)
    private User user;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Short rating;

    @Column(columnDefinition = "TEXT")
    private String content;

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

    public void deleteReview(User user){
        // 사장님이면 삭제 금지
        if(user.getRole() == UserRole.OWNER){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 관리자거나 작성자가 아닐 때 삭제 금지
        boolean isAdmin = user.getRole() == UserRole.MANAGER || user.getRole() ==UserRole.MASTER;
        boolean isUser = this.user.getUsername().equals(user.getUsername());

        if(!isAdmin && !isUser){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user.getUsername();
    }

    public void updateReview(String content, Short rating, User user){
        if(!this.user.getUsername().equals(user.getUsername())){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        this.content = content;
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = user.getUsername();
    }
}
