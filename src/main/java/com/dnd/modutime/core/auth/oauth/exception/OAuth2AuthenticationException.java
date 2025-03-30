package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * 어떤 이유로든 유효하지 않은 OAuth2 인증 객체와 관련된 모든 예외에 대한 슈퍼클래스입니다.
 */
@Getter
public class OAuth2AuthenticationException extends AuthenticationException {

    private final ErrorCode errorCode;

    public OAuth2AuthenticationException(final String msg, final Throwable cause, final ErrorCode errorCode) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public OAuth2AuthenticationException(final String msg, final ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
}
