package com.delivery.project.ai.service;

import com.delivery.project.ai.dto.request.AiRequestDto;
import com.delivery.project.ai.dto.response.AiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiService {

    Page<AiResponseDto> aiSelect(Pageable pageable, String search);

    String aiInsert(AiRequestDto dto, String username);


}