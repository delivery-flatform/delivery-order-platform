package com.delivery.project.ai.controller;

import com.delivery.project.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // TODO: POST /api/v1/ai/description - 상품 설명 생성 (50자 이하)
    // TODO: GET  /api/v1/ai/logs        - AI 로그 조회 (MANAGER+)
}
