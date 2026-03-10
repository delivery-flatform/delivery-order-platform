package com.delivery.project.region.repository;

import com.delivery.project.region.entity.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RegionRepositoryTest {

    @Autowired
    private RegionRepository regionRepository;

    private Region createRegion(String name, boolean isActive) {
        return Region.builder()
                .name(name)
                .city("서울특별시")
                .district("종로구")
                .isActive(isActive)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("활성 지역 목록 조회 성공")
    void findAllActiveRegions() {

        // given
        Region region1 = createRegion("광화문", true);
        Region region2 = createRegion("강남", true);
        Region region3 = createRegion("부산", false);

        regionRepository.save(region1);
        regionRepository.save(region2);
        regionRepository.save(region3);

        Pageable pageable = PageRequest.of(0,10);

        // when
        Page<Region> result =
                regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting("name")
                .containsExactlyInAnyOrder("광화문","강남");
    }

    @Test
    @DisplayName("활성 지역 페이징 조회")
    void findAllActiveRegionsWithPaging() {

        // given
        Region region1 = createRegion("광화문", true);
        Region region2 = createRegion("강남", true);
        Region region3 = createRegion("부산", true);

        regionRepository.saveAll(List.of(region1, region2, region3));

        Pageable pageable = PageRequest.of(0,2);

        // when
        Page<Region> result =
                regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("삭제된 지역은 목록 조회에서 제외")
    void findAllExcludeDeletedRegions() {

        // given
        Region region1 = createRegion("광화문", true);
        Region region2 = createRegion("강남", true);

        regionRepository.save(region1);
        regionRepository.save(region2);

        region1.deleteRegion("admin");
        regionRepository.save(region1);

        Pageable pageable = PageRequest.of(0,10);

        // when
        Page<Region> result =
                regionRepository.findAllByIsActiveTrueAndDeletedAtIsNull(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("강남");
    }

    @Test
    @DisplayName("활성 지역 단건 조회 성공")
    void findActiveRegionById() {

        // given
        Region region = createRegion("광화문", true);
        Region saved = regionRepository.save(region);

        // when
        Optional<Region> result =
                regionRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("광화문");
    }

    @Test
    @DisplayName("비활성 지역 조회 실패")
    void findInactiveRegionFail() {

        // given
        Region region = createRegion("광화문", false);
        Region saved = regionRepository.save(region);

        // when
        Optional<Region> result =
                regionRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(saved.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("삭제된 지역 조회 실패")
    void findDeletedRegionFail() {

        // given
        Region region = createRegion("광화문", true);
        Region saved = regionRepository.save(region);

        saved.deleteRegion("admin");
        regionRepository.save(saved);

        // when
        Optional<Region> result =
                regionRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(saved.getId());

        // then
        assertThat(result).isEmpty();
    }
}