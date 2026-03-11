package com.delivery.project.order.controller;

import com.delivery.project.global.response.ApiResponse;
import com.delivery.project.order.dto.request.OrderRequestDto;
import com.delivery.project.order.dto.request.OrderSearchRequestDto;
import com.delivery.project.order.dto.response.OrderResponseDto;
import com.delivery.project.order.entity.Order;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "주문 내역" , description = "주문 내역 관리 및 주문 상태 변경")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // TODO: GET    /api/v1/orders/{userId}&{storedId}          - 주문 목록 조회
    @GetMapping("/list")
    @Operation(summary = "주문 내역 전체 조회", description = "로그인한 사용자의 권한에 따라 본인 주문 또는 내 가게 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> selectOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Pageable validatedPageable = validatePageSize(pageable);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 서비스에 사용자의 이름(username)과 권한 목록을 넘깁니다.
        Page<OrderResponseDto> orderPage = orderService.selectOrders(
                userDetails.getUsername(),
                roles,
                validatedPageable
        );

        return ResponseEntity.ok(ApiResponse.success(orderPage));
    }

    // TODO : GET /api/v1/oredrs/listsearch - 주문 검색 조회
    @PostMapping("/listsearch")
    @Operation(summary = "주문 검색 조회", description = "로그인한 사용자의 권한에 따라 본인 주문 또는 내 가게 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> selectOrdersSearch(
            @RequestBody OrderSearchRequestDto searchDto,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Pageable validatedPageable = validatePageSize(pageable);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 서비스에 사용자의 이름(username)과 권한 목록을 넘깁니다.
        Page<OrderResponseDto> orderPage = orderService.selectOrdersSearch(
                searchDto,
                userDetails.getUsername(),
                roles,
                validatedPageable
        );

        return ResponseEntity.ok(ApiResponse.success(orderPage));
    }

    // TODO: GET    /api/v1/orders/{orderId}     - 주문 단건 조회
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 단건 조회", description = "orderId로 단건 주문을 검색해서 보여줍니다.")
    public ResponseEntity<ApiResponse<OrderResponseDto>> selectOrder(
            @PathVariable UUID orderId) {

        OrderResponseDto orderResponse = orderService.selectOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(orderResponse));
    }

    // TODO: POST   /api/v1/orders          - 주문 생성
    @PostMapping("/order")
    @Operation(summary = "주문 생성", description = "결제 전 주문 정보를 DB에 먼저 저장하고 주문 ID를 반환합니다.")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @RequestBody OrderRequestDto orderRequestDto,
            @AuthenticationPrincipal UserDetails userDetail) {

        String userId = userDetail.getUsername();
        OrderResponseDto response = orderService.insertOrder(userId, orderRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // TODO: PATCH  /api/v1/orders/{id}/cancel - 주문 취소
    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "주문 후 5분 이내인 경우에만 취소( CANCELLED )가 가능합니다.")
    public ResponseEntity<ApiResponse<OrderResponseDto>> deleteOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetail) {

        String userId = userDetail.getUsername();
        OrderResponseDto response = orderService.deleteOrder(orderId, userId);

        return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 취소되었습니다.", response));
    }

    // TODO: PATCH  /api/v1/orders/{id}/status - 주문 상태 변경
    @PatchMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 변경", description = "주문의 진행 상태( 메뉴준비중,주문수락,배달중,완료 )를 변경합니다.")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam(value = "status") Order.Status newStatus,
            @AuthenticationPrincipal UserDetails userDetail) {

        List<String> roles = userDetail.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        OrderResponseDto response = orderService.updateOrderStatus(
                orderId,
                newStatus,
                userDetail.getUsername(),
                roles
        );

        return ResponseEntity.ok(ApiResponse.success("주문 상태가 " + newStatus + "(으)로 변경되었습니다.", response));
    }

    private Pageable validatePageSize(Pageable pageable) {
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            return org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(),
                    10,
                    pageable.getSort()
            );
        }
        return pageable;
    }
}
