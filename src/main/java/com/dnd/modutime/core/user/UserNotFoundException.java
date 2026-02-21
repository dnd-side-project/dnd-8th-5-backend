package com.dnd.modutime.core.user;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.exception.AuthenticationException;

public class UserNotFoundException extends AuthenticationException {

    public UserNotFoundException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UserNotFoundException(final String message, final ErrorCode errorCode, final Throwable cause) {
        super(message, errorCode, cause);
    }
}
