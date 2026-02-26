package com.delivery.project.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_username", nullable = false, length = 100)
    private String customerUsername;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "delivery_address", length = 255)
    private String deliveryAddress;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "request_note", columnDefinition = "TEXT")
    private String requestNote;

    @Column(name = "order_type", nullable = false, length = 20)
    private String orderType;

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

    public enum Status {
        PENDING, CONFIRMED, DELIVERING, COMPLETED, CANCELLED
    }

    public enum OrderType {
        ONLINE, OFFLINE
    }
}
