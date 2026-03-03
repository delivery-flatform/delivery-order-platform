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
    public List<UserResponseDto> findAllUsers(UserRole currentRole) {
        boolean isManagerOrAbove = currentRole == UserRole.MANAGER || currentRole == UserRole.MASTER;

        if (!isManagerOrAbove) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return userRepository.findAllByDeletedAtIsNull().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    // 회원 단건 조회 (본인 또는 MANAGER+)
    public UserResponseDto findUser(String username, String currentUsername, UserRole currentRole) {
        boolean isSelf = username.equals(currentUsername);
        boolean isManagerOrAbove = currentRole == UserRole.MANAGER || currentRole == UserRole.MASTER;

        if (!isSelf && !isManagerOrAbove) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    // 회원 정보 수정 (본인만)
    @Transactional
    public UserResponseDto updateUser(String username, UserUpdateRequestDto requestDto, String currentUsername) {
        if (!username.equals(currentUsername)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

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

        return new UserResponseDto(user);
    }

    // 회원 삭제 - Soft Delete (본인 또는 MANAGER+)
    @Transactional
    public void deleteUser(String username, String currentUsername, UserRole currentRole) {
        boolean isSelf = username.equals(currentUsername);
        boolean isManagerOrAbove = currentRole == UserRole.MANAGER || currentRole == UserRole.MASTER;

        if (!isSelf && !isManagerOrAbove) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.softDelete(currentUsername);
    }

    // 권한 변경 (MANAGER+)
    @Transactional
    public UserResponseDto changeRole(String username, UserRole newRole, UserRole currentRole) {
        boolean isManagerOrAbove = currentRole == UserRole.MANAGER || currentRole == UserRole.MASTER;

        if (!isManagerOrAbove) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.changeRole(newRole);

        return new UserResponseDto(user);
    }
}
