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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public void signup(SignupRequestDto request) {

        // 중복 회원/이메일 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 역할 부여
        UserRole role = request.getRole() != null ? request.getRole() : UserRole.CUSTOMER;

        // 유저 빌드
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))        // password 인코딩
                .nickname(request.getNickname())
                .email(request.getEmail())
                .role(role)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .createdAt(LocalDateTime.now())
                .createdBy(request.getUsername())
                .build();

        // 유저 등록
        userRepository.save(user);
        log.info("회원가입 완료: {}", request.getUsername());
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto request) {
        // 유저 매칭
        User user = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // PW 매칭
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // JWT 발급
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        log.info("로그인 성공: {}", user.getUsername());

        return new LoginResponseDto(token, user.getUsername(), user.getRole().name());
    }
}
