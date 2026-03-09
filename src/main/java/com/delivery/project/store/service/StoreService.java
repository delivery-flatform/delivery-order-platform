package com.delivery.project.store.service;

import com.delivery.project.category.entity.Category;
import com.delivery.project.category.repository.CategoryRepository;
import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import com.delivery.project.review.repository.ReviewRepository;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.store.dto.response.StoreRatingResponseDto;
import com.delivery.project.store.dto.response.StoreResponseDto;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.entity.StoreCategory;
import com.delivery.project.store.repository.StoreCategoryRepository;
import com.delivery.project.store.repository.StoreRepository;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ReviewRepository reviewRepository;

    // 가게 목록 조회 (페이징, 검색)
    public Page<StoreRatingResponseDto> selectStoreList(int page, int size, String sortBy, boolean isAsc, String keyword) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Object[]> result;

        if (keyword == null || keyword.isBlank()) {
            result = storeRepository.findStoreWithRating(pageable);
        } else {
            result = storeRepository.searchStoreWithRating(keyword, pageable);
        }

        return result.map(row -> {
            Store store = (Store) row[0];
            Double rating = (Double) row[1];
            return StoreRatingResponseDto.from(store, rating);
        });
    }

    // 가게 단건 조회 (평점 평균 포함)
    public StoreRatingResponseDto selectStore(UUID id) {
        Store store = findActiveStore(id);
        Double rating = reviewRepository.findByRatingAvgWhereStoreId(id);

        log.info("가게 조회 완료 storeId={}", id);

        return StoreRatingResponseDto.from(store,  rating);
    }

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

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Transactional
    public void addCategories(UUID storeId, List<UUID> categoryIds, String username) {
        // 요청 중복 제거
        categoryIds = categoryIds.stream().distinct().toList();

        Store store = findActiveStore(storeId);
        validateStoreUpdatePermission(store, username);

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        for (Category category : categories) {
            boolean exists = storeCategoryRepository
                    .existsByStore_IdAndCategory_Id(storeId, category.getId());

            if (!exists) {
                StoreCategory storeCategory = StoreCategory.create(store, category);
                storeCategoryRepository.save(storeCategory);
            }
        }

        log.info("가게 카테고리 등록 완료 storeId={}", storeId);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Transactional
    public void removeCategories(UUID storeId, List<UUID> categoryIds, String username) {
        Store store = findActiveStore(storeId);
        validateStoreUpdatePermission(store, username);

        for (UUID categoryId : categoryIds) {
            StoreCategory storeCategory =
                    storeCategoryRepository.findByStoreIdAndCategoryId(storeId, categoryId)
                            .orElseThrow(() -> new CustomException(ErrorCode.STORE_CATEGORY_NOT_FOUND));

            storeCategoryRepository.delete(storeCategory);
        }

        log.info("가게 카테고리 삭제 완료 storeId={}", storeId);
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
                        .anyMatch(a ->
                                a.getAuthority().equals("ROLE_MANAGER") ||
                                a.getAuthority().equals("ROLE_MASTER")
                        );

        if (!isOwner && !isManagerOrMaster) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
