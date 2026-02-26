package com.delivery.project.user.service;

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
    // TODO: 회원 단건 조회
    // TODO: 회원 정보 수정
    // TODO: 회원 삭제 (Soft Delete)
    // TODO: 권한 변경 (MANAGER+)
}
