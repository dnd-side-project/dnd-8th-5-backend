package com.dnd.modutime.core.auth.oauth.exception;

import com.dnd.modutime.core.common.ErrorCode;

/**
 * OAuth2 사용자 정보를 파싱하는 과정에서 발생하는 예외
 */
public class OAuth2UserParsingException extends OAuth2AuthenticationException {

    public OAuth2UserParsingException(final String msg, final Throwable cause, final ErrorCode errorCode) {
        super(msg, cause, errorCode);
    }

    public OAuth2UserParsingException(final String msg, final ErrorCode errorCode) {
        super(msg, errorCode);
    }
}
