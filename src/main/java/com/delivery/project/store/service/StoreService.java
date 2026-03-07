package com.delivery.project.store.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;

    // TODO: 가게 목록 조회 (페이징, 검색)
    // TODO: 가게 단건 조회 (평점 평균 포함)

    // 가게 등록 (OWNER+)
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Transactional
    public StoreResponseDto insertStore(StoreRequestDto requestDto, String username) {
        // 삭제된 유저가 아니라면
        User createdUser = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User ownerUser = userRepository.findByUsernameAndDeletedAtIsNull(requestDto.getOwnerUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        // 삭제된 지역이 아니라면
        Region region = regionRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(requestDto.getRegionId())
                .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));

        Store store = Store.create(ownerUser, region, requestDto, createdUser.getUsername());

        Store savedStore = storeRepository.save(store);

        log.info("가게 등록 완료 storeId={}, storeName={}, ownername={}",
                savedStore.getId(),
                savedStore.getName(),
                savedStore.getUser().getUsername());

        return StoreResponseDto.from(store);
    }

    // TODO: 가게 수정 (OWNER 본인 or MANAGER+)
    // TODO: 가게 삭제 Soft Delete (MANAGER+)


}
