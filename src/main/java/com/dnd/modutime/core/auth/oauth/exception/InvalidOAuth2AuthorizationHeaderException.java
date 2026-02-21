package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;

public class InvalidOAuth2AuthorizationHeaderException extends OAuth2AuthenticationException {

    public InvalidOAuth2AuthorizationHeaderException(final String msg, final Throwable cause, final ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    public InvalidOAuth2AuthorizationHeaderException(final String msg, final ErrorCode errorCode) {
        super(msg, errorCode);
    }
}
