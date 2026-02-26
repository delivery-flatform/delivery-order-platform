package com.delivery.project.user.controller;

import com.delivery.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO: GET  /api/v1/users          - 회원 목록 조회 (MANAGER+)
    // TODO: GET  /api/v1/users/{username} - 회원 단건 조회
    // TODO: PUT  /api/v1/users/{username} - 회원 정보 수정
    // TODO: DELETE /api/v1/users/{username} - 회원 삭제
}
