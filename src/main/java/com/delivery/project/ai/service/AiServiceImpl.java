package com.delivery.project.ai.service;

import com.delivery.project.ai.dto.request.AiRequestDto;
import com.delivery.project.ai.dto.response.AiResponseDto;
import com.delivery.project.ai.entity.AiLog;
import com.delivery.project.ai.repository.AiLogRepository;
import com.delivery.project.global.config.AiClient;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AiServiceImpl implements AiService {

    private final AiLogRepository aiLogRepository;
    private final AiClient aiClient;

    public AiServiceImpl(AiLogRepository aiLogRepository, @Qualifier("geminiAi") AiClient aiClient){
        this.aiLogRepository = aiLogRepository;
        this.aiClient = aiClient;
    }


    // TODO: AI 로그 조회
    @Override
    public Page<AiResponseDto> aiSelect(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);

        Page<AiLog> aiLog =  aiLogRepository.findAll(pageable);
        return aiLog.map(AiResponseDto::new);
    }

    // TODO: 설명 생성 (50자 이하 제한)
    //  config로 분리해서 로직 단순화
    @Override
    @Transactional
    public String aiInsert(AiRequestDto dto) {
        String userName = "aa";

        if(!dto.isAiTrue()){
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        String responseText = aiClient.response(dto.getRequest());

        AiLog aiLog = new AiLog(dto,responseText,"gemini",userName,userName);

        aiLogRepository.save(aiLog);

        return responseText;
    }
}