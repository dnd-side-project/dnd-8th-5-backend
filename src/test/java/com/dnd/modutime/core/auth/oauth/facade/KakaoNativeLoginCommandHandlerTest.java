package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.auth.oauth.OAuth2UserDetails;
import com.dnd.modutime.core.auth.oauth.OAuth2UserResolver;
import com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse;
import com.dnd.modutime.core.auth.oauth.facade.command.KakaoNativeLoginCommand;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("KakaoNativeLoginCommandHandler")
class KakaoNativeLoginCommandHandlerTest {

    private static final String EMAIL = "user@example.com";
    private static final String OAUTH_ID = "12345";
    private static final Map<String, Object> ATTRIBUTES = Map.of("id", 12345L);

    private OAuth2UserResolver resolver;
    private OAuth2TokenProvider tokenProvider;
    private OAuth2TokenService tokenService;
    private KakaoNativeLoginCommandHandler handler;

    @BeforeEach
    void setUp() {
        this.resolver = mock(OAuth2UserResolver.class);
        this.tokenProvider = mock(OAuth2TokenProvider.class);
        this.tokenService = mock(OAuth2TokenService.class);
        this.handler = new KakaoNativeLoginCommandHandler(resolver, tokenProvider, tokenService);
    }

    @Test
    @DisplayName("사용자 조회/캐시 후 자체 JWT 발급 및 refresh token 저장")
    void 정상_처리() {
        var details = details(OAUTH_ID);
        var user = newUser(1L, OAUTH_ID, false);
        var oAuth2User = new OAuth2User(user, ATTRIBUTES, "id");
        var tokens = newTokens();

        when(resolver.resolveAndCache(eq(details), eq(ATTRIBUTES), eq("id"))).thenReturn(oAuth2User);
        when(tokenProvider.createOAuth2JwtTokenResponse(EMAIL, OAuth2Provider.KAKAO)).thenReturn(tokens);

        var command = KakaoNativeLoginCommand.of("kakao-token", "room-1")
                .bindUserDetails(details, ATTRIBUTES);

        var result = handler.handle(command);

        assertThat(result.tokens()).isSameAs(tokens);
        assertThat(result.user()).isSameAs(user);
        verify(tokenService, times(1)).saveOrUpdateOAuth2RefreshToken(EMAIL, OAuth2Provider.KAKAO, tokens);
    }

    @Test
    @DisplayName("탈퇴한 사용자는 AuthenticationException(USER_NOT_FOUND) 으로 차단")
    void 탈퇴한_사용자_차단() {
        var details = details(OAUTH_ID);
        var withdrawnUser = newUser(2L, OAUTH_ID, true);
        var oAuth2User = new OAuth2User(withdrawnUser, ATTRIBUTES, "id");

        when(resolver.resolveAndCache(eq(details), eq(ATTRIBUTES), eq("id"))).thenReturn(oAuth2User);

        var command = KakaoNativeLoginCommand.of("kakao-token", null)
                .bindUserDetails(details, ATTRIBUTES);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("탈퇴");

        verify(tokenProvider, never()).createOAuth2JwtTokenResponse(any(), any());
        verify(tokenService, never()).saveOrUpdateOAuth2RefreshToken(any(), any(), any());
    }

    private OAuth2UserDetails details(final String oauthId) {
        return new OAuth2UserDetails("이름", EMAIL, "p.jpg", "t.jpg", OAuth2Provider.KAKAO, oauthId);
    }

    private User newUser(final Long id, final String oauthId, final boolean withdrawn) {
        var user = new User("이름", EMAIL, "p.jpg", "t.jpg", OAuth2Provider.KAKAO, oauthId);
        setField(user, "id", id);
        if (withdrawn) {
            setField(user, "deletedAt", LocalDateTime.now());
        }
        return user;
    }

    private JwtTokenResponse newTokens() {
        long now = System.currentTimeMillis();
        return new JwtTokenResponse(
                "access-token-value",
                new Date(now + 60_000),
                "refresh-token-value",
                new Date(now + 1_209_600_000L)
        );
    }

    private void setField(final Object target, final String name, final Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
