package com.dnd.modutime.core.admin.application.request;

import javax.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank(message = "username은 빈 값일 수 없습니다.") String username,
        @NotBlank(message = "password는 빈 값일 수 없습니다.") String password
) {
}
