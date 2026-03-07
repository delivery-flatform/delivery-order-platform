package com.delivery.project.store.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    // 가게 수정 (OWNER 본인 or MANAGER+)
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Transactional
    public StoreResponseDto updateStore(UUID id, StoreUpdateRequestDto requestDto, String username) {
        Store store = findActiveStore(id);
        validateStoreUpdatePermission(store, username);

        store.update(requestDto, username);

        log.info("가게 수정 완료 - storeId: {}, updatedBy: {}", id, username);

        return StoreResponseDto.from(store);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Transactional
    public StoreResponseDto updateStoreStatus(UUID id, boolean isOpen, String username) {
        Store store = findActiveStore(id);
        validateStoreUpdatePermission(store, username);

        store.updateStatus(isOpen, username);

        log.info("가게 상태 변경 - storeId: {}, updatedBy: {}", id, username);

        return StoreResponseDto.from(store);
    }

    // 가게 삭제 Soft Delete (MANAGER+)
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Transactional
    public void deleteStore(UUID id, String username) {
        Store store = findStore(id);
        store.delete(username);

        log.info("가게 삭제 완료 storeId={}, ownername={}", id, store.getUser().getUsername());
    }

    private Store findStore(UUID id) {
        return storeRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
    }

    private Store findActiveStore(UUID id) {
        Store store = findStore(id);

        if (store.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.STORE_ALREADY_DELETED);
        }

        return store;
    }

    private void validateStoreUpdatePermission(Store store, String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isOwner = store.getUser().getUsername().equals(username);
        boolean isManagerOrMaster =
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")
                                || a.getAuthority().equals("ROLE_MASTER"));

        if (!isOwner && !isManagerOrMaster) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
