package com.delivery.project.user.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.user.dto.request.UserUpdateRequestDto;
import com.delivery.project.user.dto.response.UserResponseDto;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 목록 조회 (MANAGER+)
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    public List<UserResponseDto> findAllUsers() {
        List<UserResponseDto> users = userRepository.findAllByDeletedAtIsNull().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
        log.info("회원 목록 조회 완료 - 총 {}건", users.size());
        return users;
    }

    // 회원 단건 조회 (본인 또는 MANAGER+)
    @PreAuthorize("#username == authentication.name or hasAnyRole('MANAGER', 'MASTER')")
    public UserResponseDto findUser(String username) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        log.info("회원 단건 조회 완료: {}", username);
        return new UserResponseDto(user);
    }

    // 회원 정보 수정 (본인만)
    @Transactional
    @PreAuthorize("#username == authentication.name")
    public UserResponseDto updateUser(String username, UserUpdateRequestDto requestDto, String currentUsername) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이메일 변경 시 중복 확인
        if (requestDto.getEmail() != null && !requestDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
            }
        }

        // 비밀번호는 변경 시 BCrypt 인코딩
        String encodedPassword = requestDto.getPassword() != null
                ? passwordEncoder.encode(requestDto.getPassword())
                : null;

        user.update(requestDto.getNickname(), encodedPassword, requestDto.getEmail(), requestDto.getIsPublic(), currentUsername);
        log.info("회원 정보 수정 완료: {}", username);
        return new UserResponseDto(user);
    }

    // 회원 삭제 - Soft Delete (본인 또는 MANAGER+)
    @Transactional
    @PreAuthorize("#username == authentication.name or hasAnyRole('MANAGER', 'MASTER')")
    public void deleteUser(String username, String currentUsername) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.softDelete(currentUsername);
        log.info("회원 삭제(Soft Delete) 완료: {}", username);
    }

    // 권한 변경 (MANAGER+)
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    public UserResponseDto changeRole(String username, UserRole newRole) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.changeRole(newRole);
        log.info("회원 권한 변경 완료: {} → {}", username, newRole);
        return new UserResponseDto(user);
    }
}
