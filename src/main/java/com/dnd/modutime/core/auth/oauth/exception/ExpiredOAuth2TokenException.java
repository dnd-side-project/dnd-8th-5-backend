package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;

/**
 * OAuth2 토큰이 만료되었을 때 발생
 *
 * @see com.dnd.modutime.core.auth.oauth.OAuth2AuthenticationErrorCodeTranslator
 */
public class ExpiredOAuth2TokenException extends OAuth2AuthenticationException {

    public ExpiredOAuth2TokenException(final String msg, final Throwable cause, final ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    public ExpiredOAuth2TokenException(final String msg, final ErrorCode errorCode) {
        super(msg, errorCode);
    }
}
