package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.auth.oauth.exception.OAuth2AuthenticationException;
import com.dnd.modutime.core.common.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationErrorCodeTranslator {

    public static ErrorCode determineErrorCode(final AuthenticationException authException) {
        if (authException instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
            return oAuth2AuthenticationException.getErrorCode();
        }

        return ErrorCode.MT401;
    }
}
