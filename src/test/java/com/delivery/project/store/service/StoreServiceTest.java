package com.delivery.project.store.service;

import com.delivery.project.category.entity.Category;
import com.delivery.project.category.repository.CategoryRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    StoreRepository storeRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RegionRepository regionRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    StoreCategoryRepository storeCategoryRepository;

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    StoreService storeService;

    private Store store;
    private UUID storeId;
    private User owner;
    private Region region;

    @BeforeEach
    void setUp() {

        storeId = UUID.randomUUID();

        owner = mock(User.class);
        when(owner.getUsername()).thenReturn("owner");

        region = mock(Region.class);

        store = Store.builder()
                .id(storeId)
                .user(owner)
                .region(region)
                .name("치킨집")
                .minOrderPrice(15000)
                .createdBy("owner")
                .createdAt(LocalDateTime.now())
                .isOpen(true)
                .build();
    }

    @Test
    @DisplayName("가게 목록 조회 성공")
    void selectStoreList() {

        Pageable pageable = PageRequest.of(0,10);

        List<Object[]> content = new ArrayList<>();
        content.add(new Object[]{store, 4.5});

        Page<Object[]> page = new PageImpl<>(content, pageable, content.size());

        when(storeRepository.findStoreWithRating(any(Pageable.class)))
                .thenReturn(page);

        Page<StoreRatingResponseDto> result =
                storeService.selectStoreList(1,10,"createdAt",false,null);

        verify(storeRepository).findStoreWithRating(any(Pageable.class));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("가게 단건 조회 성공")
    void selectStoreSuccess() {

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(reviewRepository.findByRatingAvgWhereStoreId(storeId))
                .thenReturn(4.5);

        StoreRatingResponseDto result =
                storeService.selectStore(storeId);

        verify(storeRepository).findById(storeId);
        verify(reviewRepository).findByRatingAvgWhereStoreId(storeId);

        assertThat(result.getName()).isEqualTo("치킨집");
    }

    @Test
    @DisplayName("가게 등록 성공")
    void insertStore() {

        UUID regionId = UUID.randomUUID();

        StoreRequestDto dto = new StoreRequestDto();

        ReflectionTestUtils.setField(dto,"storeName","치킨집");
        ReflectionTestUtils.setField(dto,"ownerUsername","owner");
        ReflectionTestUtils.setField(dto,"regionId",regionId);
        ReflectionTestUtils.setField(dto,"minOrderPrice",15000);

        User creator = mock(User.class);
        when(creator.getUsername()).thenReturn("admin");

        when(userRepository.findByUsernameAndDeletedAtIsNull("admin"))
                .thenReturn(Optional.of(creator));

        when(userRepository.findByUsernameAndDeletedAtIsNull("owner"))
                .thenReturn(Optional.of(owner));

        when(regionRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(regionId))
                .thenReturn(Optional.of(region));

        when(storeRepository.save(any(Store.class)))
                .thenReturn(store);

        StoreResponseDto result =
                storeService.insertStore(dto,"admin");

        verify(userRepository).findByUsernameAndDeletedAtIsNull("admin");
        verify(userRepository).findByUsernameAndDeletedAtIsNull("owner");
        verify(regionRepository).findByIdAndIsActiveTrueAndDeletedAtIsNull(regionId);
        verify(storeRepository).save(any(Store.class));

        assertThat(result.getName()).isEqualTo("치킨집");
    }

    @Test
    @DisplayName("가게 수정 성공")
    void updateStore() {

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        StoreUpdateRequestDto dto = new StoreUpdateRequestDto();
        ReflectionTestUtils.setField(dto,"storeName","새가게");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner",null,List.of())
        );

        StoreResponseDto result =
                storeService.updateStore(storeId,dto,"owner");

        verify(storeRepository).findById(storeId);

        assertThat(result.getName()).isEqualTo("새가게");
    }

    @Test
    @DisplayName("가게 상태 변경 성공")
    void updateStoreStatus() {

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner",null,List.of())
        );

        StoreResponseDto result =
                storeService.updateStoreStatus(storeId,false,"owner");

        verify(storeRepository).findById(storeId);

        assertThat(result.getIsOpen()).isFalse();
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore() {

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        storeService.deleteStore(storeId,"admin");

        verify(storeRepository).findById(storeId);

        assertThat(store.getDeletedBy()).isEqualTo("admin");
        assertThat(store.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("카테고리 추가 성공")
    void addCategories() {

        UUID categoryId = UUID.randomUUID();

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(categoryId);

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(categoryRepository.findAllById(any()))
                .thenReturn(List.of(category));

        when(storeCategoryRepository.existsByStore_IdAndCategory_Id(storeId,categoryId))
                .thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner",null,List.of())
        );

        storeService.addCategories(storeId,List.of(categoryId),"owner");

        verify(categoryRepository).findAllById(any());
        verify(storeCategoryRepository).existsByStore_IdAndCategory_Id(storeId,categoryId);
        verify(storeCategoryRepository).save(any(StoreCategory.class));
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void removeCategories() {

        UUID categoryId = UUID.randomUUID();

        StoreCategory storeCategory = mock(StoreCategory.class);

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(storeCategoryRepository.findByStoreIdAndCategoryId(storeId,categoryId))
                .thenReturn(Optional.of(storeCategory));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner",null,List.of())
        );

        storeService.removeCategories(storeId,List.of(categoryId),"owner");

        verify(storeCategoryRepository)
                .findByStoreIdAndCategoryId(storeId,categoryId);

        verify(storeCategoryRepository)
                .delete(storeCategory);
    }

}