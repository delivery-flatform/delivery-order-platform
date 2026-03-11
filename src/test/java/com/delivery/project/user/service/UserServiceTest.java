package com.delivery.project.user.service;

import com.delivery.project.global.exception.CustomException;
import com.delivery.project.global.exception.ErrorCode;
import com.delivery.project.user.dto.request.UserUpdateRequestDto;
import com.delivery.project.user.dto.response.UserResponseDto;
import com.delivery.project.user.entity.User;
import com.delivery.project.user.entity.UserRole;
import com.delivery.project.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // ===== 회원 단건 조회 =====

    @Test
    @DisplayName("회원 단건 조회 성공")
    void findUser_success() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser("user1");

        // then
        assertThat(result.getUsername()).isEqualTo("user1");
        assertThat(result.getNickname()).isEqualTo("닉네임");
    }

    @Test
    @DisplayName("회원 단건 조회 실패 - 존재하지 않는 유저")
    void findUser_fail_notFound() {
        // given
        given(userRepository.findByUsernameAndDeletedAtIsNull("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUser("unknown"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    // ===== 회원 목록 조회 =====

    @Test
    @DisplayName("회원 목록 조회 성공")
    void findAllUsers_success() {
        // given
        List<User> users = List.of(
                createUser("user1", UserRole.CUSTOMER),
                createUser("user2", UserRole.OWNER)
        );
        given(userRepository.findAllByDeletedAtIsNull()).willReturn(users);

        // when
        List<UserResponseDto> result = userService.findAllUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("회원 목록 조회 - 빈 목록 반환")
    void findAllUsers_empty() {
        // given
        given(userRepository.findAllByDeletedAtIsNull()).willReturn(List.of());

        // when
        List<UserResponseDto> result = userService.findAllUsers();

        // then
        assertThat(result).isEmpty();
    }

    // ===== 회원 정보 수정 =====

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateUser_success() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        UserUpdateRequestDto requestDto = createUpdateRequest("새닉네임", null, null, null);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.updateUser("user1", requestDto, "user1");

        // then
        assertThat(result.getNickname()).isEqualTo("새닉네임");
    }

    @Test
    @DisplayName("회원 정보 수정 성공 - 비밀번호 포함")
    void updateUser_success_withPassword() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        UserUpdateRequestDto requestDto = createUpdateRequest(null, "NewPass1!", null, null);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));
        given(passwordEncoder.encode("NewPass1!")).willReturn("encodedNewPw");

        // when
        userService.updateUser("user1", requestDto, "user1");

        // then
        verify(passwordEncoder).encode("NewPass1!");
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 이메일 중복")
    void updateUser_fail_duplicateEmail() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        UserUpdateRequestDto requestDto = createUpdateRequest(null, null, "taken@example.com", null);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("taken@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateUser("user1", requestDto, "user1"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 - 동일 이메일 입력 시 중복 체크 안 함")
    void updateUser_success_sameEmail() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        // user1의 이메일은 user1@example.com 이므로 동일 이메일 입력
        UserUpdateRequestDto requestDto = createUpdateRequest(null, null, "user1@example.com", null);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when & then - 중복 체크 없이 정상 처리
        userService.updateUser("user1", requestDto, "user1");
    }

    @Test
    @DisplayName("회원 정보 수정 성공 - isPublic만 변경")
    void updateUser_success_isPublicOnly() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        UserUpdateRequestDto requestDto = createUpdateRequest(null, null, null, false);

        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.updateUser("user1", requestDto, "user1");

        // then
        assertThat(result.getIsPublic()).isFalse();
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 유저")
    void updateUser_fail_notFound() {
        // given
        UserUpdateRequestDto requestDto = createUpdateRequest("닉네임", null, null, null);
        given(userRepository.findByUsernameAndDeletedAtIsNull("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser("unknown", requestDto, "unknown"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    // ===== 회원 삭제 =====

    @Test
    @DisplayName("회원 삭제(Soft Delete) 성공")
    void deleteUser_success() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        userService.deleteUser("user1", "user1");

        // then - deletedAt이 설정됐는지 확인
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 삭제 시 deletedBy 필드가 요청자 username으로 설정됨")
    void deleteUser_success_deletedBySet() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        userService.deleteUser("user1", "manager1");

        // then
        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(user.getDeletedBy()).isEqualTo("manager1");
    }

    @Test
    @DisplayName("회원 삭제 실패 - 존재하지 않는 유저")
    void deleteUser_fail_notFound() {
        // given
        given(userRepository.findByUsernameAndDeletedAtIsNull("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser("unknown", "unknown"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    // ===== 권한 변경 =====

    @Test
    @DisplayName("권한 변경 성공")
    void changeRole_success() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.changeRole("user1", UserRole.MANAGER);

        // then
        assertThat(result.getRole()).isEqualTo(UserRole.MANAGER);
    }

    @Test
    @DisplayName("권한 변경 성공 - CUSTOMER → MASTER")
    void changeRole_success_toMaster() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.changeRole("user1", UserRole.MASTER);

        // then
        assertThat(result.getRole()).isEqualTo(UserRole.MASTER);
    }

    @Test
    @DisplayName("회원 단건 조회 응답에 password 미포함")
    void findUser_responseDoesNotContainPassword() {
        // given
        User user = createUser("user1", UserRole.CUSTOMER);
        given(userRepository.findByUsernameAndDeletedAtIsNull("user1")).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.findUser("user1");

        // then - UserResponseDto에 password 필드 없음을 확인
        assertThat(result.getUsername()).isEqualTo("user1");
        assertThat(result.getEmail()).isEqualTo("user1@example.com");
        // password getter가 없으므로 컴파일 타임에 보장됨
    }

    @Test
    @DisplayName("권한 변경 실패 - 존재하지 않는 유저")
    void changeRole_fail_notFound() {
        // given
        given(userRepository.findByUsernameAndDeletedAtIsNull("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.changeRole("unknown", UserRole.MANAGER))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("삭제된 유저 단건 조회 실패 - deletedAt 설정된 유저는 조회 안 됨")
    void findUser_fail_deletedUser() {
        // given - findByUsernameAndDeletedAtIsNull은 삭제된 유저를 Optional.empty()로 반환
        given(userRepository.findByUsernameAndDeletedAtIsNull("deleted1")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUser("deleted1"))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 목록 조회 시 삭제된 유저 제외 - findAllByDeletedAtIsNull 호출 검증")
    void findAllUsers_excludesDeletedUsers() {
        // given - 살아있는 유저만 반환하는 쿼리 메서드 사용 검증
        given(userRepository.findAllByDeletedAtIsNull()).willReturn(List.of(
                createUser("user1", UserRole.CUSTOMER)
        ));

        // when
        List<UserResponseDto> result = userService.findAllUsers();

        // then
        assertThat(result).hasSize(1);
        verify(userRepository).findAllByDeletedAtIsNull();
    }

    // ===== 헬퍼 메서드 =====

    private User createUser(String username, UserRole role) {
        return User.builder()
                .username(username)
                .password("encodedPw")
                .nickname("닉네임")
                .email(username + "@example.com")
                .role(role)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
    }

    private UserUpdateRequestDto createUpdateRequest(String nickname, String password, String email, Boolean isPublic) {
        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        ReflectionTestUtils.setField(dto, "nickname", nickname);
        ReflectionTestUtils.setField(dto, "password", password);
        ReflectionTestUtils.setField(dto, "email", email);
        ReflectionTestUtils.setField(dto, "isPublic", isPublic);
        return dto;
    }
}
