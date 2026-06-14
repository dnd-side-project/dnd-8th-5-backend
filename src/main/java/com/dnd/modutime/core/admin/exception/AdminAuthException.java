package com.dnd.modutime.core.admin.exception;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.exception.AuthenticationException;

/**
 * 어드민 로그인/토큰 재발급 등 컨트롤러 계층에서 발생하는 인증 실패 예외.
 *
 * <p>{@link com.dnd.modutime.advice.GlobalControllerAdvice} 가 401 응답으로 변환한다.</p>
 */
public class AdminAuthException extends AuthenticationException {

    public AdminAuthException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }
}
