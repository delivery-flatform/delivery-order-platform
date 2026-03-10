package com.delivery.project.review.repository;

import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.product.entity.Product;
import com.delivery.project.product.repository.ProductRepository;
import com.delivery.project.region.entity.Region;
import com.delivery.project.region.repository.RegionRepository;
import com.delivery.project.review.dto.request.ReviewUpdateRequestDto;
import com.delivery.project.review.dto.response.ReviewResponseDto;
import com.delivery.project.review.entity.Review;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    User user;
    Order order;
    Store store;
    Product product;
    Review review;
    Review saveReview;
    Region region;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("green")
                .password("qwer1234!!")
                .nickname("그린")
                .email("green@naver.com")
                .role(UserRole.CUSTOMER)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .createdBy("green")
                .build();

        userRepository.saveAndFlush(user);

        region = Region.builder()
                .city("서울")
                .name("집")
                .district("강남구")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .createdBy("green")
                .build();

        regionRepository.saveAndFlush(region);

        store = Store.builder()
                .user(user)
                .name("김치찌개")
                .isOpen(true)
                .region(region)
                .createdAt(LocalDateTime.now())
                .createdBy("green")
                .minOrderPrice(5000)
                .address("서울 강남구")
                .build();

        storeRepository.saveAndFlush(store);

        product = Product.builder()
                .store(store)
                .createdAt(LocalDateTime.now())
                .createdBy("green")
                .isHidden(false)
                .price(5000)
                .name("김밥")
                .build();

        productRepository.saveAndFlush(product);

        order = Order.builder()
                .customerUsername(user.getUsername())
                .status(Order.Status.COMPLETED)
                .totalPrice(25000)
                .createdAt(LocalDateTime.now())
                .createdBy("apple")
                .productId(product.getId())
                .storeId(store.getId())
                .orderType(Order.OrderType.ONLINE)
                .createdBy("apple")
                .deliveryAddress("서울 강남구")
                .build();

        orderRepository.saveAndFlush(order);

        review = Review.builder()
                .rating((short) 5)
                .content("맛있었어요")
                .user(user)
                .order(order)
                .store(store)
                .createdAt(LocalDateTime.now())
                .createdBy(user.getNickname())
                .build();

        saveReview = reviewRepository.save(review);
    }

    @Test
    @DisplayName("리뷰 생성 성공 테스트")
    void review_create_success() {
        ReviewResponseDto responseDto = ReviewResponseDto.from(saveReview);

        assertThat(responseDto.getContent()).isEqualTo("맛있었어요");
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    void review_update_success() {
        UUID id = saveReview.getId();

        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("별로예요", (short) 3);

        saveReview.updateReview(requestDto.getContent(), requestDto.getRating(), user);

        reviewRepository.save(saveReview);

        assertThat(saveReview.getContent()).isEqualTo("별로예요");
        assertThat(saveReview.getRating()).isEqualTo((short) 3);
        assertThat(saveReview.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    void review_delete_success() {

        saveReview.deleteReview(user);

        reviewRepository.save(saveReview);

        Review deleteReview = reviewRepository.findById(saveReview.getId()).orElseThrow();

        assertThat(deleteReview.getId()).isEqualTo(saveReview.getId());
        assertThat(deleteReview.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트 - 관리자")
    void review_delete_admin_success(){
        user.changeRole(UserRole.MANAGER);

        saveReview.deleteReview(user);

        Review deleteReview = reviewRepository.findById(saveReview.getId()).orElseThrow();

        assertThat(deleteReview.getUser().getRole()).isEqualTo(saveReview.getUser().getRole());
        assertThat(deleteReview.getDeletedAt()).isNotNull();

    }

    @Test
    @DisplayName("리뷰 조회 성공 테스트")
    void review_select_success() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewRepository.findByUserUsernameAndDeletedAtIsNullAndContentContaining(
                user.getUsername(), "맛", pageable
        );

        assertThat(reviews.map(Review::getContent)).isNotNull();
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(saveReview.getId());

    }

    @Test
    @DisplayName("리뷰 조회 성공 테스트 - OWNER")
    void review_select_fail(){
        user.changeRole(UserRole.OWNER);

        userRepository.save(user);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewRepository.findByStoreIdAndDeletedAtIsNull(
                store.getId(), pageable
        );

        assertThat(reviews.map(Review::getContent)).isNotNull();
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(saveReview.getId());
    }
}
