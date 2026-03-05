package com.delivery.project.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequestDto {

    @Size(max = 10, message = "닉네임은 최대 10자입니다.")
    private String nickname;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$",
            message = "비밀번호는 8~15자, 영문/숫자/특수문자를 포함해야 합니다."
    )
    private String password;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private Boolean isPublic;
}
