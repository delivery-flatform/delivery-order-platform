package com.delivery.project.auth.controller;

import com.delivery.project.auth.dto.response.LoginResponseDto;
import com.delivery.project.auth.service.AuthService;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // ===== 회원가입 =====

    @Test
    @WithMockUser
    @DisplayName("회원가입 성공 - 200 반환")
    void signup_success() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "Test1234!",
                    "nickname": "테스터",
                    "email": "test@example.com",
                    "role": "CUSTOMER",
                    "isPublic": true
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - username 누락 시 400")
    void signup_fail_missingUsername() throws Exception {
        String body = """
                {
                    "password": "Test1234!",
                    "nickname": "테스터",
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - username 중복 시 409")
    void signup_fail_duplicateUsername() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "Test1234!",
                    "nickname": "테스터",
                    "email": "test@example.com"
                }
                """;

        doThrow(new CustomException(ErrorCode.DUPLICATE_USERNAME))
                .when(authService).signup(any());

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - username 3자(최소 4자 미만) 시 400")
    void signup_fail_shortUsername() throws Exception {
        String body = """
                {
                    "username": "usr",
                    "password": "Test1234!",
                    "nickname": "테스터",
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - 비밀번호 특수문자 없음 시 400")
    void signup_fail_weakPassword() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "Test12345",
                    "nickname": "테스터",
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - 이메일 형식 오류 시 400")
    void signup_fail_invalidEmail() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "Test1234!",
                    "nickname": "테스터",
                    "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - 이메일 중복 시 409")
    void signup_fail_duplicateEmail() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "Test1234!",
                    "nickname": "테스터",
                    "email": "taken@example.com"
                }
                """;

        doThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL))
                .when(authService).signup(any());

        mockMvc.perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ===== 로그인 =====

    @Test
    @WithMockUser
    @DisplayName("로그인 성공 - 토큰 반환")
    void login_success() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "Test1234!"
                }
                """;

        LoginResponseDto response = new LoginResponseDto("jwt-token", "testuser", "CUSTOMER");
        given(authService.login(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 실패 - username 누락 시 400")
    void login_fail_missingUsername() throws Exception {
        String body = """
                {
                    "password": "Test1234!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 실패 - 존재하지 않는 유저 404")
    void login_fail_userNotFound() throws Exception {
        String body = """
                {
                    "username": "unknown",
                    "password": "Test1234!"
                }
                """;

        given(authService.login(any()))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 성공 - 응답에 role 포함")
    void login_success_responseContainsRole() throws Exception {
        String body = """
                {
                    "username": "manager1",
                    "password": "Test1234!"
                }
                """;

        LoginResponseDto response = new LoginResponseDto("jwt-token", "manager1", "MANAGER");
        given(authService.login(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("MANAGER"));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 실패 - 비밀번호 불일치 시 401")
    void login_fail_invalidPassword() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "wrongPw!"
                }
                """;

        given(authService.login(any()))
                .willThrow(new CustomException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
