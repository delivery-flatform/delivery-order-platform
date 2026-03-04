package com.delivery.project.region.controller;

import com.delivery.project.global.security.UserDetailsImpl;
import com.delivery.project.region.dto.RegionRequestDto;
import com.delivery.project.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    // TODO: GET    /api/v1/regions       - 지역 목록 조회
    // TODO: GET    /api/v1/regions/{id}  - 지역 단건 조회

    // 지역 등록
    @PostMapping
    public void insertRegion(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RegionRequestDto requestDto) {
        regionService.insertRegion(userDetails, requestDto);
    }

    // TODO: PUT    /api/v1/regions/{id}  - 지역 수정
    // TODO: DELETE /api/v1/regions/{id}  - 지역 삭제
}
