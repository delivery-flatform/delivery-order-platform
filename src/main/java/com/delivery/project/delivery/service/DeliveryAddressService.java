package com.delivery.project.delivery.service;

import com.delivery.project.delivery.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;

    // TODO: 배송지 목록 조회
    // TODO: 배송지 등록
    // TODO: 배송지 수정
    // TODO: 배송지 삭제 Soft Delete
    // TODO: 기본 배송지 설정
}
