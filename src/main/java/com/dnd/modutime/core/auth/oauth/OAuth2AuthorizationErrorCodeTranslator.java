package com.dnd.modutime.core.auth.oauth;

import com.dnd.modutime.core.common.ErrorCode;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

public class OAuth2AuthorizationErrorCodeTranslator {

    private static final Map<Class<? extends AccessDeniedException>, ErrorCode> exceptionToErrorCode = Map.of(
    );

    public static ErrorCode determineErrorCode(final AccessDeniedException authorizationException) {
        return exceptionToErrorCode.getOrDefault(authorizationException.getClass(), ErrorCode.MT403);
    }
}
