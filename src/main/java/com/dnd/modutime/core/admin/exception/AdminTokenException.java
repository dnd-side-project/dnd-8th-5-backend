package com.dnd.modutime.core.admin.exception;

import com.dnd.modutime.core.common.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * 어드민 토큰 검증 실패 시 필터 계층에서 던지는 예외.
 *
 * <p>Spring Security 의 {@link AuthenticationException} 을 상속하므로
 * {@link com.dnd.modutime.core.admin.security.AdminAuthenticationEntryPoint} 가 401 응답으로 직렬화한다.</p>
 */
@Getter
public class AdminTokenException extends AuthenticationException {

    private final ErrorCode errorCode;

    public AdminTokenException(final String msg, final ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
}
