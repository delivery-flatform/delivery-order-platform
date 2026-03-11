package com.delivery.project.user.controller;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.user.dto.response.UserResponseDto;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // ===== 회원 단건 조회 =====

    @Test
    @DisplayName("회원 단건 조회 성공 - 200 반환")
    void getUser_success() throws Exception {
        UserResponseDto response = createUserResponse("user1", UserRole.CUSTOMER);
        given(userService.findUser("user1")).willReturn(response);

        mockMvc.perform(get("/api/v1/users/user1")
                        .with(mockUserDetails("user1", UserRole.CUSTOMER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("user1"));
    }

    @Test
    @DisplayName("회원 단건 조회 실패 - 미인증 시 401")
    void getUser_fail_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/user1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 단건 조회 실패 - 없는 유저 404")
    void getUser_fail_notFound() throws Exception {
        given(userService.findUser("unknown"))
                .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/users/unknown")
                        .with(mockUserDetails("unknown", UserRole.CUSTOMER)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ===== 회원 목록 조회 =====

    @Test
    @DisplayName("회원 목록 조회 성공 - 200 반환")
    void getAllUsers_success() throws Exception {
        List<UserResponseDto> users = List.of(
                createUserResponse("user1", UserRole.CUSTOMER),
                createUserResponse("user2", UserRole.OWNER)
        );
        given(userService.findAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .with(mockUserDetails("manager1", UserRole.MANAGER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    // ===== 회원 정보 수정 =====

    @Test
    @DisplayName("회원 정보 수정 성공 - 200 반환")
    void updateUser_success() throws Exception {
        UserResponseDto response = createUserResponse("user1", UserRole.CUSTOMER);
        given(userService.updateUser(anyString(), any(), anyString())).willReturn(response);

        String body = """
                {
                    "nickname": "새닉네임"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user1")
                        .with(csrf())
                        .with(mockUserDetails("user1", UserRole.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ===== 회원 삭제 =====

    @Test
    @DisplayName("회원 삭제 성공 - 200 반환")
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user1")
                        .with(csrf())
                        .with(mockUserDetails("user1", UserRole.CUSTOMER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("회원 삭제 실패 - 없는 유저 404")
    void deleteUser_fail_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.USER_NOT_FOUND))
                .when(userService).deleteUser(anyString(), anyString());

        mockMvc.perform(delete("/api/v1/users/unknown")
                        .with(csrf())
                        .with(mockUserDetails("unknown", UserRole.CUSTOMER)))
                .andExpect(status().isNotFound());
    }

    // ===== 권한 변경 =====

    @Test
    @DisplayName("권한 변경 성공 - 200 반환")
    void changeRole_success() throws Exception {
        UserResponseDto response = createUserResponse("user1", UserRole.MANAGER);
        given(userService.changeRole(anyString(), any())).willReturn(response);

        String body = """
                {
                    "role": "MANAGER"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/user1/role")
                        .with(csrf())
                        .with(mockUserDetails("manager1", UserRole.MANAGER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("MANAGER"));
    }

    @Test
    @DisplayName("회원 목록 조회 실패 - 미인증 시 401")
    void getAllUsers_fail_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 유저 404")
    void updateUser_fail_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.USER_NOT_FOUND))
                .when(userService).updateUser(anyString(), any(), anyString());

        String body = """
                {
                    "nickname": "새닉네임"
                }
                """;

        mockMvc.perform(put("/api/v1/users/unknown")
                        .with(csrf())
                        .with(mockUserDetails("unknown", UserRole.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 미인증 시 401")
    void updateUser_fail_unauthorized() throws Exception {
        String body = """
                {
                    "nickname": "새닉네임"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 삭제 실패 - 미인증 시 401")
    void deleteUser_fail_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("권한 변경 실패 - role 누락 시 400")
    void changeRole_fail_missingRole() throws Exception {
        String body = """
                {}
                """;

        mockMvc.perform(patch("/api/v1/users/user1/role")
                        .with(csrf())
                        .with(mockUserDetails("manager1", UserRole.MANAGER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한 변경 실패 - 존재하지 않는 유저 404")
    void changeRole_fail_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.USER_NOT_FOUND))
                .when(userService).changeRole(anyString(), any());

        String body = """
                {
                    "role": "MANAGER"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/unknown/role")
                        .with(csrf())
                        .with(mockUserDetails("manager1", UserRole.MANAGER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("권한 변경 실패 - 미인증 시 401")
    void changeRole_fail_unauthorized() throws Exception {
        String body = """
                {
                    "role": "MANAGER"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/user1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 이메일 형식 오류 시 400")
    void updateUser_fail_invalidEmail() throws Exception {
        String body = """
                {
                    "email": "not-an-email"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user1")
                        .with(csrf())
                        .with(mockUserDetails("user1", UserRole.CUSTOMER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== 헬퍼 메서드 =====

    private UserResponseDto createUserResponse(String username, UserRole role) {
        User user = User.builder()
                .username(username)
                .password("encodedPw")
                .nickname("닉네임")
                .email(username + "@example.com")
                .role(role)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
        return new UserResponseDto(user);
    }

    // UserDetailsImpl을 principal로 주입하는 헬퍼
    private RequestPostProcessor mockUserDetails(
            String username, UserRole role) {
        User user = User.builder()
                .username(username)
                .password("encodedPw")
                .nickname("닉네임")
                .email(username + "@example.com")
                .role(role)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authentication(auth);
    }
}
