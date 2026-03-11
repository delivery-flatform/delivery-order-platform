package com.delivery.project.ai.entity;

import com.delivery.project.ai.dto.request.AiRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_ai_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "target_type", nullable = false, length = 30)
    private TargetTypeEnum targetType;

    @Column(length = 1000)
    private String prompt;

    @Column(length = 1000)
    private String response;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    // FK 미설정 - 로그 특성상 유저 삭제 후에도 기록 보존
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
