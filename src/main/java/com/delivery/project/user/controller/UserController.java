package com.delivery.project.user.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.user.dto.request.UserRoleUpdateRequestDto;
import com.delivery.project.user.dto.request.UserUpdateRequestDto;
import com.delivery.project.user.dto.response.UserResponseDto;
import com.delivery.project.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 목록 조회 (MANAGER+)
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.findAllUsers(userDetails.getUser().getRole())
                )
        );
    }

    // 회원 단건 조회 (본인 또는 MANAGER+)
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.findUser(username, userDetails.getUsername(), userDetails.getUser().getRole())
                )
        );
    }

    // 회원 정보 수정 (본인만)
    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.updateUser(username, requestDto, userDetails.getUsername())
                )
        );
    }

    // 회원 삭제 - Soft Delete (본인 또는 MANAGER+)
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(username, userDetails.getUsername(), userDetails.getUser().getRole());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 권한 변경 (MANAGER+)
    @PatchMapping("/{username}/role")
    public ResponseEntity<ApiResponse<UserResponseDto>> changeRole(
            @PathVariable String username,
            @Valid @RequestBody UserRoleUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.changeRole(username, requestDto.getRole(), userDetails.getUser().getRole())
                )
        );
    }
}
