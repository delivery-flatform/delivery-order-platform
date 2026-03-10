package com.delivery.project.review.service;

import com.delivery.project.order.entity.Order;
import com.delivery.project.order.repository.OrderRepository;
import com.delivery.project.review.dto.request.ReviewRequestDto;
import com.delivery.project.review.dto.request.ReviewUpdateRequestDto;
import com.delivery.project.review.dto.response.ReviewResponseDto;
import com.delivery.project.review.dto.response.ReviewUpdateResponseDto;
import com.delivery.project.review.entity.Review;
import com.delivery.project.review.repository.ReviewRepository;
import com.delivery.project.store.entity.Store;
import com.delivery.project.store.repository.StoreRepository;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ReviewService reviewService;

    User user;
    UUID id = UUID.randomUUID();
    Review review;
    Order order;
    Store store;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("green")
                .role(UserRole.CUSTOMER)
                .build();

        order = Order.builder()
                .customerUsername("green")
                .status(Order.Status.COMPLETED)
                .build();

        store = Store.builder()
                .name("김치찌개")
                .build();

        review = Review.builder()
                .content("맛있었어요")
                .rating((short)5)
                .user(user)
                .order(order)
                .store(store)
                .build();
    }


    @Test
    @DisplayName("리뷰 생성 성공 테스트")
    void review_insert_success() {

        ReviewRequestDto dto = new ReviewRequestDto(id, id, "맛있어요ㅠㅠ", (short) 1.0);

        // findById -> any() 어떤 값이든, Status -> eq() 무조건 괄호 안에 값으로
        given(orderRepository.findByIdAndStatus(any(), eq(Order.Status.COMPLETED)))
                .willReturn(Optional.of(order));

        given(storeRepository.findById(any())).willReturn(Optional.of(store));

        // 리뷰가 존재하지 않도록
        given(reviewRepository.existsByOrderId(any())).willReturn(false);

        // 어떤 review객체가 들어와도 , 0번째 파라미터로 들어온 객체를 돌려줌
        given(reviewRepository.save(any(Review.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ReviewResponseDto response = reviewService.createReview(dto, user);

        // 1번만 실행되었는지
        verify(reviewRepository, times(1)).save(any(Review.class));
        assertThat(response.getContent()).isEqualTo("맛있어요ㅠㅠ");
        assertThat(response).isNotNull();

    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    void review_update_success() {

        ReviewUpdateRequestDto dto = new ReviewUpdateRequestDto("별로였어요", (short) 1);

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));

        ReviewUpdateResponseDto response = reviewService.updateReview(id, dto, user);

        assertThat(response.getContent()).isEqualTo("별로였어요");
        assertThat(response).isNotNull();

    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    void review_delete_success() {

        // findById -> eq(id)인 값을 리턴.
        given(reviewRepository.findByIdAndDeletedAtIsNull(eq(id))).willReturn(Optional.of(review));

        // when
        reviewService.deleteReview(id, user);

        // then
        assertThat(review.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("리뷰 조회 테스트 - 유저")
    void review_select_test(){
        Pageable pageable = PageRequest.of(0,10);

        Page<Review> page = new PageImpl<>(List.of(review));

        given(reviewRepository.findByUserUsernameAndDeletedAtIsNullAndContentContaining(eq(user.getUsername()),eq("맛"), eq(pageable)))
                .willReturn(page);

        Page<ReviewResponseDto> dto = reviewService.selectReview(pageable,"맛",user);

        assertThat(1).isEqualTo(dto.getTotalElements());
    }

    @Test
    @DisplayName("리뷰 조회 테스트 - 가게")
    void review_owner_select_test(){

        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(List.of(review));

        User ownerUser = User.builder()
                .username("green")
                .role(UserRole.OWNER)
                .build();

        // 서비스에서 if()문 통과를 위해 store 객체 다시 세팅
        ReflectionTestUtils.setField(store, "name", "green");

        given(storeRepository.findById(id)).willReturn(Optional.of(store));
        given(reviewRepository.findByStoreIdAndDeletedAtIsNull(id, pageable)).willReturn(page);

        Page<ReviewResponseDto> result = reviewService.selectReview(id, pageable, null, ownerUser);

        assertThat(result.getTotalElements()).isEqualTo(1);

    }




}
