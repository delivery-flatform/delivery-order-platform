package com.delivery.project.ai.dto.response;

import com.delivery.project.ai.entity.AiLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AiResponseDto {
    private String user_id;
    private String prompt;
    private String request;
    private LocalDateTime create_at;

    public AiResponseDto(AiLog aiLog){
        this.user_id = aiLog.getUserName();
        this.prompt = aiLog.getPrompt();
        this.request = aiLog.getRequest();
        this.create_at = aiLog.getCreatedAt();
    }

}