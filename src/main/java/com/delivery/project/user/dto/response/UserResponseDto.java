package com.delivery.project.user.dto.response;

import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {

    private final String username;
    private final String nickname;
    private final String email;
    private final UserRole role;
    private final Boolean isPublic;
    private final LocalDateTime createdAt;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.isPublic = user.getIsPublic();
        this.createdAt = user.getCreatedAt();
    }
}
