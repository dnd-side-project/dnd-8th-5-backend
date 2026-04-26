package com.dnd.modutime.core.user.controller.dto;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record UserWithdrawRequest(
        @NotBlank(message = "탈퇴 사유는 필수값 입니다.")
        @Size(max = 200, message = "탈퇴 사유는 200자 이하로 입력해주세요.")
        String reason,

        @AssertTrue(message = "데이터 영구 삭제 동의가 필요합니다.")
        boolean agreed
) {
}
