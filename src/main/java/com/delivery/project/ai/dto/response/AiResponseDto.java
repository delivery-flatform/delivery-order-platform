package com.delivery.project.ai.dto.response;

import com.delivery.project.ai.entity.AiLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AiResponseDto {
    private String user_id;
    private String prompt;
    private String response;
    private LocalDateTime create_at;

    public static AiResponseDto from(AiLog aiLog){
        return AiResponseDto.builder()
                .user_id(aiLog.getUserName())
                .prompt(aiLog.getPrompt())
                .response(aiLog.getResponse())
                .create_at(aiLog.getCreatedAt())
                .build();
    }

}