package com.delivery.project.ai.service;

import com.delivery.project.ai.dto.request.AiRequestDto;
import com.delivery.project.ai.dto.response.AiResponseDto;
import org.springframework.data.domain.Page;

public interface AiService {

    Page<AiResponseDto> aiSelect(int page, int size, String sortBy, boolean isAsc, String search);

    String aiInsert(AiRequestDto dto);


}