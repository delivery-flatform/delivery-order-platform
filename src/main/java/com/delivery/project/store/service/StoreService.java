package com.delivery.project.store.service;

import com.delivery.project.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    // TODO: 가게 목록 조회 (페이징, 검색)
    // TODO: 가게 단건 조회 (평점 평균 포함)
    // TODO: 가게 등록 (OWNER+)
    // TODO: 가게 수정 (OWNER 본인 or MANAGER+)
    // TODO: 가게 삭제 Soft Delete (MANAGER+)
}
