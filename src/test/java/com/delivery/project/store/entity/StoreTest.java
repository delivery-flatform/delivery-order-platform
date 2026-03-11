package com.delivery.project.store.entity;

import com.delivery.project.region.entity.Region;
import com.delivery.project.store.dto.request.StoreRequestDto;
import com.delivery.project.store.dto.request.StoreUpdateRequestDto;
import com.delivery.project.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StoreTest {

    @Test
    @DisplayName("Store 생성 테스트")
    void createStore() {

        User user = mock(User.class);
        Region region = mock(Region.class);

        StoreRequestDto dto = new StoreRequestDto();

        ReflectionTestUtils.setField(dto, "storeName", "치킨집");
        ReflectionTestUtils.setField(dto, "description", "맛있는 치킨");
        ReflectionTestUtils.setField(dto, "phone", "01012345678");
        ReflectionTestUtils.setField(dto, "address", "서울시 강남구");
        ReflectionTestUtils.setField(dto, "minOrderPrice", 15000);

        Store store = Store.create(user, region, dto, "owner");

        assertThat(store.getName()).isEqualTo("치킨집");
        assertThat(store.getDescription()).isEqualTo("맛있는 치킨");
        assertThat(store.getPhone()).isEqualTo("01012345678");
        assertThat(store.getAddress()).isEqualTo("서울시 강남구");
        assertThat(store.getMinOrderPrice()).isEqualTo(15000);
        assertThat(store.getIsOpen()).isTrue();
        assertThat(store.getCreatedBy()).isEqualTo("owner");
    }

    @Test
    @DisplayName("Store 수정 테스트")
    void updateStore() {

        Store store = Store.builder()
                .name("기존가게")
                .description("기존설명")
                .phone("01000000000")
                .address("기존주소")
                .minOrderPrice(10000)
                .build();

        StoreUpdateRequestDto dto = new StoreUpdateRequestDto();

        ReflectionTestUtils.setField(dto, "storeName", "새가게");
        ReflectionTestUtils.setField(dto, "description", "새설명");
        ReflectionTestUtils.setField(dto, "phone", "01099999999");
        ReflectionTestUtils.setField(dto, "address", "새주소");
        ReflectionTestUtils.setField(dto, "minOrderPrice", 20000);

        store.update(dto, "admin");

        assertThat(store.getName()).isEqualTo("새가게");
        assertThat(store.getDescription()).isEqualTo("새설명");
        assertThat(store.getPhone()).isEqualTo("01099999999");
        assertThat(store.getAddress()).isEqualTo("새주소");
        assertThat(store.getMinOrderPrice()).isEqualTo(20000);
        assertThat(store.getUpdatedBy()).isEqualTo("admin");
        assertThat(store.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Store 삭제 테스트")
    void deleteStore() {

        Store store = Store.builder().build();

        store.delete("owner");

        assertThat(store.getDeletedBy()).isEqualTo("owner");
        assertThat(store.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Store 영업 상태 변경")
    void updateStatus() {

        Store store = Store.builder()
                .isOpen(true)
                .build();

        store.updateStatus(false, "owner");

        assertThat(store.getIsOpen()).isFalse();
        assertThat(store.getUpdatedBy()).isEqualTo("owner");
        assertThat(store.getUpdatedAt()).isNotNull();
    }
}