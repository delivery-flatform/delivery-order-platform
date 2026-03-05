package com.delivery.project.user.dto.request;

import com.delivery.project.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserRoleUpdateRequestDto {

    @NotNull(message = "변경할 권한을 입력해주세요.")
    private UserRole role;
}
