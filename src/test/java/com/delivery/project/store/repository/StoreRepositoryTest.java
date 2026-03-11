package com.delivery.project.store.repository;

import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;


    private User createUser() {
        return userRepository.save(
                User.builder()
                        .username("owner")
                        .password("1234")
                        .nickname("owner")
                        .email("owner@test.com")
                        .role(UserRole.OWNER)
                        .isPublic(true)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    private Region createRegion() {
        return regionRepository.save(
                Region.builder()
                        .name("광화문")
                        .city("서울")
                        .district("종로구")
                        .isActive(true)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    private Store createStore(String name, User user, Region region) {
        return Store.builder()
                .name(name)
                .user(user)
                .region(region)
                .address("서울 종로구")
                .phone("01012341234")
                .minOrderPrice(15000)
                .isOpen(true)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("가게 이름으로 조회 성공")
    void findByName() {

        User user = createUser();
        Region region = createRegion();

        Store store = createStore("치킨집", user, region);
        storeRepository.save(store);

        Optional<Store> result =
                storeRepository.findByNameAndDeletedAtIsNull("치킨집");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("치킨집");
    }


    @Test
    @DisplayName("삭제된 가게 조회 실패")
    void findDeletedStoreFail() {

        User user = createUser();
        Region region = createRegion();

        Store store = createStore("치킨집", user, region);
        Store saved = storeRepository.save(store);

        saved.delete("admin");

        Optional<Store> result =
                storeRepository.findByNameAndDeletedAtIsNull("치킨집");

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("가게 목록 조회 성공 (평점 포함)")
    void findStoreWithRating() {

        User user = createUser();
        Region region = createRegion();

        storeRepository.save(createStore("치킨집", user, region));
        storeRepository.save(createStore("피자집", user, region));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Object[]> result =
                storeRepository.findStoreWithRating(pageable);

        assertThat(result.getContent()).hasSize(2);

        Object[] row = result.getContent().get(0);

        assertThat(row[0]).isInstanceOf(Store.class);
        assertThat(row[1]).isInstanceOf(Double.class);
    }


    @Test
    @DisplayName("가게 검색 조회 성공")
    void searchStore() {

        User user = createUser();
        Region region = createRegion();

        storeRepository.save(createStore("치킨집", user, region));
        storeRepository.save(createStore("피자집", user, region));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Object[]> result =
                storeRepository.searchStoreWithRating("치킨", pageable);

        assertThat(result.getContent()).hasSize(1);

        Store store = (Store) result.getContent().get(0)[0];

        assertThat(store.getName()).isEqualTo("치킨집");
    }
}