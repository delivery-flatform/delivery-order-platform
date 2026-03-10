package com.delivery.project.ai.service;

import com.delivery.project.ai.dto.request.AiRequestDto;
import com.delivery.project.ai.dto.response.AiResponseDto;
import com.delivery.project.ai.entity.AiLog;
import com.delivery.project.ai.entity.TargetTypeEnum;
import com.delivery.project.ai.repository.AiLogRepository;
import com.delivery.project.global.config.AiClient;
import com.delivery.project.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest{

    @Mock
    private AiLogRepository aiLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiClient aiClient;

    @InjectMocks
    private AiServiceImpl aiService;

    AiLog aiLog = AiLog.builder()
            .targetType(TargetTypeEnum.PRODUCT_DESCRIPTION)
            .prompt("치킨 메뉴 설명 해줘.")
            .response("바삭바삭한 치킨입니다.")
            .userName("green")
            .modelName("gemini")
            .createdBy("green")
            .createdAt(LocalDateTime.now()).build();

    @Test
    @DisplayName("ai 상품 설명 생성 성공 테스트")
    void aiInsert_Success() {
        // given = 클라이언트에서 넘어 온 dto 설정
        AiRequestDto dto = new AiRequestDto(true, "치킨 메뉴 설명");

        // aiClient의 response메소드 안에 dto객체를 넣으면 응답값 return
        when(aiClient.response(dto.getRequest())).thenReturn("바삭한 치킨입니다.");

        // aiInsert 실행
        aiService.aiInsert(dto,"green");

        // then => 가짜 객체인 aiClient.response(어느말이든)가 1번 실행되었는지.
        verify(aiClient, times(1)).response(anyString());

    }

    @Test
    @DisplayName("ai 로그 전체 조회 성공 테스트")
    void aiSelect_Success(){
        Pageable pageable = PageRequest.of(0,10);

        // mock을 사용하여 가짜 객체 1개 생성
        List<AiLog> aiLogs= List.of(mock(AiLog.class));

        // aiLog page로 감싸기
        Page<AiLog> page = new PageImpl<>(aiLogs);

        // findAll을 실행하면 page 객체로 반환하도록 설정 (DB 조회 x)
        when(aiLogRepository.findAll(pageable)).thenReturn(page);

        Page<AiResponseDto> responseDto = aiService.aiSelect(pageable,null);

        // findAll이 실제로 실행됐는지 확인. 결과 값이 1인지 확인(위에서 생성 한 것은 1개)
        verify(aiLogRepository).findAll(pageable);
        assertThat(1).isEqualTo(responseDto.getTotalElements());
    }

    @Test
    @DisplayName("ai 로그 검색 조회 성공 테스트")
    void aiSearchSelect_Success(){
        Pageable pageable = PageRequest.of(0,10);

        List<AiLog> aiLogs = List.of(aiLog);

        Page<AiLog> page = new PageImpl<>(aiLogs);

        when(aiLogRepository.findByPromptContainingOrResponseContaining("치킨","치킨",pageable))
                .thenReturn(page);

        Page<AiResponseDto> result = aiService.aiSelect(pageable,"치킨");

        verify(aiLogRepository).findByPromptContainingOrResponseContaining("치킨","치킨",pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);

    }
}
