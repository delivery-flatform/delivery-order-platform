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
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.delivery.project.global.util.PageableUtil.createPageable;

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
    @PreAuthorize("hasAnyRole('MASTER','MANAGER')")
    public Page<AiResponseDto> aiSelect(int page, int size, String sortBy, boolean isAsc, String search) {

        Pageable pageable = createPageable(page,size,sortBy,isAsc);

        Page<AiLog> aiLog;

        if(search == null || search.isBlank()){
            aiLog =  aiLogRepository.findAll(pageable);
        }else{
            // 프롬프트나 응답 결과에 검색어가 있으면 값이 나올 수 있게
            aiLog = aiLogRepository.findByPromptContainingOrResponseContaining(search,search,pageable);
        }

        return aiLog.map(AiResponseDto::from);
    }

    // TODO: 설명 생성 (50자 이하 제한)
    //  config로 분리해서 로직 단순화
    @Override
    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public String aiInsert(AiRequestDto dto) {
        String userName = "aa"; // user값 받아와서 수정해줘야함.

        if(!dto.isAiTrue()){
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        String responseText = aiClient.response(dto.getRequest());

        AiLog aiLog = AiLog.builder()
                .prompt(dto.getRequest())
                .modelName("gemini")
                .targetType(dto.getTargetType())
                .createdBy(userName)
                .createdAt(LocalDateTime.now())
                .userName(userName)
                .response(responseText)
                .build();

        aiLogRepository.save(aiLog);

        return responseText;
    }

}