package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;
import lombok.Getter;

/**
 * 카카오 사용자가 이메일 제공에 동의하지 않은 경우.
 * 서비스가 이메일을 식별자로 사용하므로 가입을 거절하고 HTTP 403 으로 응답한다
 * ({@link com.dnd.modutime.advice.GlobalControllerAdvice} 에서 매핑).
 */
@Getter
public class KakaoEmailNotProvidedException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.KAKAO_EMAIL_NOT_PROVIDED;

    public KakaoEmailNotProvidedException(final String message) {
        super(message);
    }

    public String getCode() {
        return this.errorCode.getCode();
    }
}
