package com.delivery.project.user.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.user.dto.response.UserResponseDto;
import com.delivery.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO: GET  /api/v1/users          - 회원 목록 조회 (MANAGER+)
    // 회원 단건 조회
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {  // SecurityContext에서 자동 주입
        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getUser(username, userDetails.getUsername(), userDetails.getUser().getRole())
                )
        );
    }

    // TODO: PUT  /api/v1/users/{username} - 회원 정보 수정
    // TODO: DELETE /api/v1/users/{username} - 회원 삭제
}
