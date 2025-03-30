package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.exception.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InvalidTokenException(final String message, final ErrorCode errorCode, final Throwable cause) {
        super(message, errorCode, cause);
    }
}
