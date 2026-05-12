package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.exception.AuthenticationException;

/**
 * 네이티브 앱이 전달한 카카오 access token 이 카카오 측에서 거절된 경우(401/403).
 * 컨트롤러에서 throw 되어 {@link com.dnd.modutime.advice.GlobalControllerAdvice}의
 * AuthenticationException 핸들러를 통해 HTTP 401 응답으로 변환된다.
 */
public class InvalidKakaoAccessTokenException extends AuthenticationException {

    public InvalidKakaoAccessTokenException(final String message) {
        super(message, ErrorCode.INVALID_KAKAO_TOKEN);
    }

    public InvalidKakaoAccessTokenException(final String message, final Throwable cause) {
        super(message, ErrorCode.INVALID_KAKAO_TOKEN, cause);
    }
}
