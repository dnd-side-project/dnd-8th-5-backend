package com.dnd.modutime.core.infrastructure.common;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * 외부 클라이언트 공용 설정.
 *
 * @param host                 호스트 주소 (예: https://kapi.kakao.com)
 * @param authenticationKey    인증 키 (선택 — null/빈 값이면 헤더 미주입)
 * @param connectTimeout       연결 시간 초과
 * @param readTimeout          응답 시간 초과
 */
@Validated
public record ClientProperties(
        @NotBlank(message = "호스트 주소는 필수입력값입니다.")
        String host,

        String authenticationKey,

        @NotNull(message = "연결시간초과는 필수입력값입니다.")
        Duration connectTimeout,

        @NotNull(message = "응답시간초과는 필수입력값입니다.")
        Duration readTimeout
) {

    public boolean hasAuthenticationKey() {
        return StringUtils.hasText(this.authenticationKey);
    }
}
