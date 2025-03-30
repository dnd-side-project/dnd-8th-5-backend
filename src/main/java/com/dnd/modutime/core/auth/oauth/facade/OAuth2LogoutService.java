package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.dto.OAuth2JwtSubject;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2LogoutService {

    private final OAuth2TokenProvider oAuth2TokenProvider;
    private final UserRepository userRepository;

    public OAuth2LogoutService(final OAuth2TokenProvider oAuth2TokenProvider, final UserRepository userRepository) {
        this.oAuth2TokenProvider = oAuth2TokenProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public void logout(final String accessToken) {
        Claims claims = this.oAuth2TokenProvider.getOAuth2TokenClaims(accessToken);

        String subject = claims.getSubject();
        OAuth2JwtSubject parsedOAuth2JwtSubject = new OAuth2JwtSubject(subject);

        String email = parsedOAuth2JwtSubject.getEmail();
        String registrationId = parsedOAuth2JwtSubject.getRegistrationId();

        OAuth2Provider provider = OAuth2Provider.findByRegistrationId(registrationId);

        var user = this.userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

        user.expireRefreshToken();
    }

}
