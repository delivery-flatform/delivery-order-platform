package com.delivery.project.order.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.order.dto.request.OrderRequestDto;
import com.delivery.project.order.dto.request.OrderSearchRequestDto;
import com.delivery.project.order.dto.response.OrderResponseDto;
import com.delivery.project.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "주문 내역" , description = "주문 내역 관리 및 주문 상태 변경")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // TODO: GET    /api/v1/orders/{userId}&{storedId}          - 주문 목록 조회
    @GetMapping("/list")
    //@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER','CUSTOMER')")
    @Operation(summary = "주문 내역 전체 조회", description = "userId와 storeId로 주문 내역을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> selectOrders(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "storeId", required = false) String storeId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrderResponseDto> orderPage = orderService.selectOrders(userId, storeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orderPage));
    }

    // TODO : GET /api/v1/oredrs/listsearch/{userId}&{storedId} - 주문 검색 조회
    @PostMapping("/listsearch")
    //@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER','CUSTOMER')")
    @Operation(summary = "주문 내역 검색 조회", description = "userId와 storeId로 주문 내역을 검색 후 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> selectOrdersSearch(
            @RequestBody OrderSearchRequestDto searchDto,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrderResponseDto> orderPage = orderService.selectOrdersSearch(searchDto, pageable);
        return ResponseEntity.ok(ApiResponse.success(orderPage));
    }

    // TODO: GET    /api/v1/orders/{orderid}     - 주문 단건 조회
    @GetMapping("/{orderId}")
    //@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER', 'CUSTOMER')")
    @Operation(summary = "주문 단건 조회", description = "orderId로 단건 주문을 검색해서 보여줍니다.")
    public ResponseEntity<ApiResponse<OrderResponseDto>> selectOrder(
            @PathVariable UUID orderId) {

        OrderResponseDto orderResponse = orderService.selectOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(orderResponse));
    }

    // TODO: POST   /api/v1/orders          - 주문 생성
    @PostMapping("/oreder")
    //@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> creatOrder(@RequestBody OrderRequestDto orderRequestDto,
                                                                    @RequestParam String userId) {

        OrderResponseDto response = orderService.createOrder(userId, orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    // TODO: PATCH  /api/v1/orders/{id}/cancel - 주문 취소
    // TODO: PATCH  /api/v1/orders/{id}/status - 주문 상태 변경
}
