package com.delivery.project.global.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "test-secret-key-1234567890abcdefgh-very-secure-key";
    private static final long EXPIRATION = 86400000L; // 1일

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    @DisplayName("토큰 생성 성공")
    void generateToken_success() {
        // given
        String username = "testuser";
        String role = "CUSTOMER";

        // when
        String token = jwtUtil.generateToken(username, role);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("토큰에서 username 추출 성공")
    void getUsername_success() {
        // given
        String username = "testuser";
        String token = jwtUtil.generateToken(username, "CUSTOMER");

        // when
        String extracted = jwtUtil.getUsername(token);

        // then
        assertThat(extracted).isEqualTo(username);
    }

    @Test
    @DisplayName("토큰에서 role 추출 성공")
    void getRole_success() {
        // given
        String role = "MANAGER";
        String token = jwtUtil.generateToken("testuser", role);

        // when
        String extracted = jwtUtil.getRole(token);

        // then
        assertThat(extracted).isEqualTo(role);
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_validToken_returnsTrue() {
        // given
        String token = jwtUtil.generateToken("testuser", "CUSTOMER");

        // when
        boolean result = jwtUtil.validateToken(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 토큰 검증 실패")
    void validateToken_invalidToken_returnsFalse() {
        // given
        String invalidToken = "invalid.token.value";

        // when
        boolean result = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateToken_expiredToken_returnsFalse() {
        // given - 만료 시간을 0으로 설정해서 즉시 만료
        JwtUtil expiredJwtUtil = new JwtUtil(SECRET, 0L);
        String token = expiredJwtUtil.generateToken("testuser", "CUSTOMER");

        // when
        boolean result = expiredJwtUtil.validateToken(token);

        // then
        assertThat(result).isFalse();
    }
}
