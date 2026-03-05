package com.delivery.project.global.config;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("geminiAi")
@RequiredArgsConstructor
public class AiClient {

    @Value("${spring.gemini.url}")
    private String url;

    @Value("${spring.gemini.api-key}")
    private String apiKey;

    private final WebClient webClient;

    // ai 생성 로직
    public String response(String request) {
        // 받아 온 prompt 설정
        String persona = request + "에 대한 설명을 작성해줘." +
                "1.답은 꼭 한가지로만 50자 이하로 대답해줘." +
                "2. 글자 수 표시나 이런 쓸데없는건 다 제외하고 설명에 대한 답만 해줘.";

        // gemini로 보낼 body
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                        Map.of("text", persona)
                                )
                        )
                )
        );

        // url 주소
        String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .build().toString();


        JsonNode response = webClient.post()
                .uri(fullUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return Optional.ofNullable(response.at("/candidates/0/content/parts/0/text").asText(null))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}