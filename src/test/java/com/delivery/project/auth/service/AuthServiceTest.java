package com.delivery.project.auth.service;

import com.delivery.project.auth.dto.request.LoginRequestDto;
import com.delivery.project.auth.dto.request.SignupRequestDto;
import com.delivery.project.auth.dto.response.LoginResponseDto;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.global.jwt.JwtUtil;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    // ===== 회원가입 =====

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequestDto request = createSignupRequest("user1", "test@example.com");
        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPw");

        // when
        authService.signup(request);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void signup_fail_duplicateUsername() {
        // given
        SignupRequestDto request = createSignupRequest("user1", "test@example.com");
        given(userRepository.existsByUsername("user1")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_USERNAME);
    }

    @Test
    @DisplayName("회원가입 실패 - email 중복")
    void signup_fail_duplicateEmail() {
        // given
        SignupRequestDto request = createSignupRequest("user1", "test@example.com");
        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("회원가입 시 role 미입력이면 CUSTOMER로 기본 설정")
    void signup_defaultRole_isCustomer() {
        // given
        SignupRequestDto request = createSignupRequest("user1", "test@example.com");
        ReflectionTestUtils.setField(request, "role", null);

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPw");

        // when
        authService.signup(request);

        // then
        verify(userRepository).save(any(User.class));
    }

    // ===== 로그인 =====

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequestDto request = createLoginRequest("user1", "Test1234!");
        User user = createUser("user1", "encodedPw", UserRole.CUSTOMER);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("Test1234!", "encodedPw")).willReturn(true);
        given(jwtUtil.generateToken("user1", "CUSTOMER")).willReturn("jwt-token");

        // when
        LoginResponseDto response = authService.login(request);

        // then
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 유저")
    void login_fail_userNotFound() {
        // given
        LoginRequestDto request = createLoginRequest("unknown", "Test1234!");
        given(userRepository.findByUsernameAndDeletedAtIsNull("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        // given
        LoginRequestDto request = createLoginRequest("user1", "wrongPw!");
        User user = createUser("user1", "encodedPw", UserRole.CUSTOMER);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPw!", "encodedPw")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
    }

    @Test
    @DisplayName("로그인 실패 - 삭제된 유저")
    void login_fail_deletedUser() {
        // given
        LoginRequestDto request = createLoginRequest("user1", "Test1234!");
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    // ===== 헬퍼 메서드 =====

    private SignupRequestDto createSignupRequest(String username, String email) {
        SignupRequestDto dto = new SignupRequestDto();
        ReflectionTestUtils.setField(dto, "username", username);
        ReflectionTestUtils.setField(dto, "password", "Test1234!");
        ReflectionTestUtils.setField(dto, "nickname", "닉네임");
        ReflectionTestUtils.setField(dto, "email", email);
        ReflectionTestUtils.setField(dto, "role", UserRole.CUSTOMER);
        ReflectionTestUtils.setField(dto, "isPublic", true);
        return dto;
    }

    private LoginRequestDto createLoginRequest(String username, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        ReflectionTestUtils.setField(dto, "username", username);
        ReflectionTestUtils.setField(dto, "password", password);
        return dto;
    }

    private User createUser(String username, String password, UserRole role) {
        return User.builder()
                .username(username)
                .password(password)
                .nickname("닉네임")
                .email("test@example.com")
                .role(role)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
    }
}
