package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserCache;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2LogoutService 테스트")
class OAuth2LogoutServiceTest {

    private static final String ACCESS_TOKEN = "mock-access-token";
    private static final String EMAIL = "test@example.com";
    private static final String SUBJECT = "kakao:" + EMAIL; // OAuth2JwtSubject 포맷
    private static final String EXPECTED_CACHE_KEY = "kakao:" + EMAIL;

    @Mock
    private OAuth2TokenProvider oAuth2TokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCache userCache;

    @Mock
    private Claims claims;

    @Mock
    private User user;

    @InjectMocks
    private OAuth2LogoutService oAuth2LogoutService;

    @DisplayName("로그아웃 시 refreshToken 만료 후 OAuth2User 캐시를 무효화한다")
    @Test
    void 로그아웃_성공_캐시_무효화() {
        // given
        when(oAuth2TokenProvider.getOAuth2TokenClaims(ACCESS_TOKEN)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(SUBJECT);
        when(userRepository.findByEmailAndProvider(EMAIL, OAuth2Provider.KAKAO))
                .thenReturn(Optional.of(user));

        // when
        oAuth2LogoutService.logout(ACCESS_TOKEN);

        // then
        verify(user).expireRefreshToken();
        verify(userCache).removeUserFromCache(eq(EXPECTED_CACHE_KEY));
    }

    @DisplayName("사용자를 찾을 수 없으면 UserNotFoundException을 던지고 캐시는 건드리지 않는다")
    @Test
    void 사용자_미존재_시_캐시_미호출() {
        // given
        when(oAuth2TokenProvider.getOAuth2TokenClaims(ACCESS_TOKEN)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(SUBJECT);
        when(userRepository.findByEmailAndProvider(EMAIL, OAuth2Provider.KAKAO))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> oAuth2LogoutService.logout(ACCESS_TOKEN))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(user, never()).expireRefreshToken();
        verify(userCache, never()).removeUserFromCache(eq(EXPECTED_CACHE_KEY));
    }
}
