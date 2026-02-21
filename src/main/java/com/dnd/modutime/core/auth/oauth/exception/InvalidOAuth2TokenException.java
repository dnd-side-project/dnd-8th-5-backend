package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;

/**
 * OAuth2 토큰이 유효하지 않을 때 발생
 *
 * @see com.dnd.modutime.core.auth.oauth.OAuth2AuthenticationErrorCodeTranslator
 */
public class InvalidOAuth2TokenException extends OAuth2AuthenticationException {

    public InvalidOAuth2TokenException(final String msg, final Throwable cause, final ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    public InvalidOAuth2TokenException(final String msg, final ErrorCode errorCode) {
        super(msg, errorCode);
    }
}
