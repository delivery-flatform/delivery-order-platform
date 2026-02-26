package com.delivery.project.ai.service;

import com.delivery.project.ai.repository.AiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiService {

    private final AiLogRepository aiLogRepository;
    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    // TODO: 상품 설명 생성 (50자 이하 제한)
    // TODO: AI 로그 조회
}
