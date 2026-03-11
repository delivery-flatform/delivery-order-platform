package com.delivery.project.order.entity;

import com.delivery.project.product.entity.Product;
import com.delivery.project.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
@Schema(description = "주문 엔티티")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_username", referencedColumnName = "username", insertable = false, updatable = false)
    @Schema(description = "주문 고객 정보 (연관 관계)")
    private User customer;

    @Column(name = "customer_username", nullable = false, length = 100)
    @Schema(description = "고객 유저네임", example = "sparta_user")
    private String customerUsername;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @Schema(description = "주문 상품 정보 (연관 관계)")
    private Product product;

    @Column(name = "product_id", nullable = true)
    @Schema(description = "상품 ID (여러 개의 상품 저장)")
    private UUID productId;

    @Column(name = "store_id", nullable = false)
    @Schema(description = "가게 ID")
    private UUID storeId;

    @Column(name = "delivery_address", length = 255)
    @Schema(description = "배송 주소", example = "서울특별시 강남구 ...")
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Schema(description = "주문 상태", allowableValues = {"PENDING", "CONFIRMED", "DELIVERING", "COMPLETED", "CANCELLED"})
    private Status status;

    @Column(name = "total_price", nullable = false)
    @Schema(description = "총 주문 금액", example = "25000")
    private Integer totalPrice;

    @Column(name = "request_note", columnDefinition = "TEXT")
    @Schema(description = "고객 요청 사항", example = "문 앞에 두고 벨 눌러주세요.")
    private String requestNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    @Schema(description = "주문 유형", example = "ONLINE")
    private OrderType orderType;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100, nullable = false)
    @Schema(description = "생성자")
    private String createdBy;

    @Column(name = "updated_at")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    @Schema(description = "수정자")
    private String updatedBy;

    @Column(name = "deleted_at")
    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    @Schema(description = "삭제자")
    private String deletedBy;

    public enum Status {
        READY, PENDING, CONFIRMED, DELIVERING, COMPLETED, CANCELLED
    }

    public enum OrderType {
        ONLINE, OFFLINE
    }

    public void cancel() {
        // 이미 취소된 상태라면 중복 처리 방지
        if (this.status == Status.CANCELLED) {
            throw new IllegalArgumentException("이미 취소된 주문입니다.");
        }

        // 상태 변경
        this.status = Status.CANCELLED;

        // 수정 시간 및 수정자 갱신
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(Status newStatus, String updatedBy) {
        // 이미 취소되거나 완료된 주문은 변경 불가
        if (this.status == Status.CANCELLED || this.status == Status.COMPLETED) {
            throw new IllegalArgumentException("이미 완료되었거나 취소된 주문은 상태를 변경할 수 없습니다.");
        }

        // 논리적 흐름 검증 (예: PENDING -> DELIVERING은 가능하지만, COMPLETED -> PENDING은 불가)
        validateStatusTransition(this.status, newStatus);

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    private void validateStatusTransition(Status current, Status next) {
        boolean isValid = false;

        switch (current) {
            case READY:
                // 결제가 완료되면 PENDING(주문 대기) 상태로 변경 가능
                if (next == Status.PENDING || next == Status.CANCELLED) isValid = true;
                break;

            case PENDING:
                // 대기 중일 때는 접수(CONFIRMED) 또는 취소(CANCELLED)만 가능
                if (next == Status.CONFIRMED || next == Status.CANCELLED) isValid = true;
                break;

            case CONFIRMED:
                // 접수 후에는 배달 시작(DELIVERING)만 가능
                if (next == Status.DELIVERING) isValid = true;
                break;

            case DELIVERING:
                // 배달 중에는 배달 완료(COMPLETED)만 가능
                if (next == Status.COMPLETED) isValid = true;
                break;

            default:
                isValid = false;
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                    String.format("상태를 변경할 수 없습니다: [%s] -> [%s]", current, next)
            );
        }
    }
}