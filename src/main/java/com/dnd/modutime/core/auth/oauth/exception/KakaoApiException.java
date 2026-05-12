package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;
import lombok.Getter;

/**
 * 카카오 API 호출 자체가 실패한 경우 (5xx / 네트워크 오류 / 타임아웃 등).
 * 인증 실패가 아니라 외부 인프라 장애이므로 HTTP 500 으로 응답한다.
 */
@Getter
public class KakaoApiException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.KAKAO_API_ERROR;

    public KakaoApiException(final String message) {
        super(message);
    }

    public KakaoApiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return this.errorCode.getCode();
    }
}
