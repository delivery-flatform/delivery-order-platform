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
    @DisplayName("권한 변경 실패 - 존재하지 않는 유저")
    void changeRole_fail_notFound() {
        // given
        given(userRepository.findByUsernameAndDeletedAtIsNull("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.changeRole("unknown", UserRole.MANAGER))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
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
