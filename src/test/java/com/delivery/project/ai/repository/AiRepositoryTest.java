package com.delivery.project.ai.repository;

import com.delivery.project.ai.dto.response.AiResponseDto;
import com.delivery.project.ai.entity.AiLog;
import com.delivery.project.ai.entity.TargetTypeEnum;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AiRepositoryTest {

    @Autowired
    private AiLogRepository aiLogRepository;

    User user;
    AiLog aiLog;

    @BeforeEach
    void setUp(){
        user = User.builder()
                .username("green")
                .password("qwer1234!!")
                .nickname("그린")
                .email("green@naver.com")
                .role(UserRole.OWNER)
                .createdAt(LocalDateTime.now())
                .createdBy("green")
                .build();

        aiLog = AiLog.builder()
                .id(UUID.randomUUID())
                .targetType(TargetTypeEnum.PRODUCT_DESCRIPTION)
                .prompt("치킨 메뉴 설명 해줘.")
                .response("바삭바삭한 치킨입니다.")
                .userName(user.getUsername())
                .modelName("gemini")
                .createdBy(user.getUsername())
                .createdAt(LocalDateTime.now()).build();
    }



    @Test
    @DisplayName("ai로그 저장 테스트")
    void ai_save_Test(){

        AiLog saveAiLog = aiLogRepository.save(aiLog);

        AiResponseDto dto = AiResponseDto.from(saveAiLog);

        assertThat(dto.getResponse()).isNotNull();

    }

    @Test
    @DisplayName("ai 로그 조회 테스트")
    void ai_select_Test(){

        aiLogRepository.save(aiLog);

        Pageable pageable = PageRequest.of(0,10);

        Page<AiLog> aiLogs = aiLogRepository.findAll(pageable);

        assertThat(aiLogs.map(AiLog::getPrompt)).contains("치킨 메뉴 설명 해줘.");

    }

    @Test
    @DisplayName("ai 로그 검색 테스트")
    void ai_search_Test(){

        // given
        AiLog aiLog2 = AiLog.builder()
                .targetType(TargetTypeEnum.PRODUCT_DESCRIPTION)
                .prompt("파스타 메뉴 설명 해줘.")
                .response("토마토 소스 가득 파스타입니다.")
                .userName(user.getUsername())
                .modelName("gemini")
                .createdBy(user.getUsername())
                .createdAt(LocalDateTime.now()).build();

        // when
        aiLogRepository.save(aiLog);
        aiLogRepository.save(aiLog2);

        Pageable pageable = PageRequest.of(0,10);

        Page<AiLog> aiLogs =
        aiLogRepository.findByPromptContainingOrResponseContaining("치킨","치킨",pageable);

        // then
        // 원하는 결과값 1개
        assertThat(aiLogs.getTotalElements()).isEqualTo(1);

    }

}
