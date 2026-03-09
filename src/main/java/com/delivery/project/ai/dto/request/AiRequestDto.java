package com.delivery.project.ai.dto.request;

import com.delivery.project.ai.entity.TargetTypeEnum;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AiRequestDto {

    private boolean aiTrue;
    // private TargetTypeEnum targetType;

    @Size(max = 20, message = "질문은 20자 이하로 작성해주세요.")
    private String request;

}