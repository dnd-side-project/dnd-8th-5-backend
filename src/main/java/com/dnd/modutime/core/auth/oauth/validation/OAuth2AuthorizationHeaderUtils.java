package com.dnd.modutime.core.auth.oauth.validation;

import com.dnd.modutime.core.auth.oauth.exception.InvalidOAuth2AuthorizationHeaderException;
import com.dnd.modutime.core.auth.security.GrantType;
import com.dnd.modutime.core.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class OAuth2AuthorizationHeaderUtils {

    private OAuth2AuthorizationHeaderUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void validateAuthorization(String authorizationHeader) {
        log.debug("authorizationHeader: {}", authorizationHeader);

        // authorizationHeader 필수 체크
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new InvalidOAuth2AuthorizationHeaderException("Authorization header가 없거나 빈값입니다.", ErrorCode.INVALID_AUTHORIZATION_HEADER);
        }
        // authorizationHeader Bearer 체크
        String[] authorizations = authorizationHeader.split(" ");
        if (authorizations.length < 2 || (!GrantType.BEARER.getType().equals(authorizations[0]))) {
            throw new InvalidOAuth2AuthorizationHeaderException("인증 타입이 Bearer 타입이 아닙니다.", ErrorCode.INVALID_AUTHORIZATION_HEADER);
        }
    }
}
