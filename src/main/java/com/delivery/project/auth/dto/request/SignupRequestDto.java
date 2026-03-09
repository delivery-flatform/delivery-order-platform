package com.delivery.project.auth.dto.request;

import com.delivery.project.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$",
        message = "비밀번호는 8~15자이며 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private UserRole role;

    private Boolean isPublic = true;
}
