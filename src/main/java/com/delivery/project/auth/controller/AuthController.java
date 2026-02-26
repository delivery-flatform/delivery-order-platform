package com.delivery.project.auth.controller;

import com.delivery.project.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO: POST /api/v1/auth/signup - 회원가입
    // TODO: POST /api/v1/auth/login  - 로그인 (JWT 발급)
}
