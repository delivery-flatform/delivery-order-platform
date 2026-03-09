package com.delivery.project.store.repository;

import com.delivery.project.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    Optional<Store> findByNameAndDeletedAtIsNull(String name);

    // 가게 목록 조회 + 리뷰 평균 평점
    @EntityGraph(attributePaths = {"region"})
    @Query("""
        select s, coalesce(avg(r.rating),0)
        from Store s
        left join Review r
            on r.store.id = s.id
            and r.deletedAt is null
        where s.deletedAt is null
        group by s
    """)
    Page<Object[]> findStoreWithRating(Pageable pageable);

    // 가게 검색 + 리뷰 평균 평점
    @EntityGraph(attributePaths = {"region"})
    @Query("""
        select s, coalesce(avg(r.rating),0)
        from Store s
        left join Review r
            on r.store.id = s.id
            and r.deletedAt is null
        where s.deletedAt is null
          and s.name like concat('%', :keyword, '%')
        group by s
    """)
    Page<Object[]> searchStoreWithRating(@Param("keyword") String keyword, Pageable pageable);

    Optional<Store> findByOwnerUsernameAndDeletedAtIsNull(String username);
}
