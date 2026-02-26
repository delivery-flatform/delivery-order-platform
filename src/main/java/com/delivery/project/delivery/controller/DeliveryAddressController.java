package com.delivery.project.delivery.controller;

import com.delivery.project.delivery.service.DeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery-addresses")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    // TODO: GET    /api/v1/delivery-addresses       - 배송지 목록 조회
    // TODO: POST   /api/v1/delivery-addresses       - 배송지 등록
    // TODO: PUT    /api/v1/delivery-addresses/{id}  - 배송지 수정
    // TODO: DELETE /api/v1/delivery-addresses/{id}  - 배송지 삭제
}
