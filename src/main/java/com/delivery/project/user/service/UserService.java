package com.delivery.project.user.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.user.dto.response.UserResponseDto;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // TODO: 회원 목록 조회

    // 회원 단건 조회 (본인 또는 MANAGER+)
    public UserResponseDto getUser(String username, String currentUsername, UserRole currentRole) {
        boolean isSelf = username.equals(currentUsername);
        boolean isManagerOrAbove = currentRole == UserRole.MANAGER || currentRole == UserRole.MASTER;

        if (!isSelf && !isManagerOrAbove) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    // TODO: 회원 정보 수정
    // TODO: 회원 삭제 (Soft Delete)
    // TODO: 권한 변경 (MANAGER+)
}
