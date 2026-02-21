package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.exception.InvalidOAuth2TokenException;
import com.dnd.modutime.core.auth.security.TokenType;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserCache;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2TokenProvider 테스트")
class OAuth2TokenProviderTest {

    private OAuth2TokenProvider oAuth2TokenProvider;

    private TokenConfigurationProperties tokenConfigurationProperties;

    @Mock
    private UserCache userCache;

    private static final String SECRET_KEY = "this-is-a-test-secret-key-for-testing-purposes-only";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String PROVIDER_ID = "kakao";

    @BeforeEach
    void setUp() {
        // Record는 mock하기 어려우므로 실제 객체 생성
        tokenConfigurationProperties = new TokenConfigurationProperties(
                "3600000",      // accessTokenExpirationTime - 1 hour
                "604800000",    // refreshTokenExpirationTime - 7 days
                SECRET_KEY,     // secret
                false           // secureCookie
        );

        oAuth2TokenProvider = new OAuth2TokenProvider(
                tokenConfigurationProperties,
                mock(UserRepository.class),
                userCache
        );
    }

    @Test
    @DisplayName("createOAuth2AccessToken()에서 생성한 토큰에 token_type: ACCESS claim이 포함되어야 한다")
    void testAccessTokenContainsTokenTypeAccessClaim() {
        // Given
        OAuth2Provider provider = OAuth2Provider.KAKAO;

        // When
        String accessToken = oAuth2TokenProvider.createOAuth2AccessToken(TEST_EMAIL, provider);

        // Then
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(accessToken)
                .getBody();

        String tokenType = claims.get("token_type", String.class);
        assertThat(tokenType).isEqualTo(TokenType.ACCESS.name());
    }

    @Test
    @DisplayName("createOAuth2RefreshToken()에서 생성한 토큰에 token_type: REFRESH claim이 포함되어야 한다")
    void testRefreshTokenContainsTokenTypeRefreshClaim() {
        // Given
        OAuth2Provider provider = OAuth2Provider.KAKAO;

        // When
        String refreshToken = oAuth2TokenProvider.createOAuth2RefreshToken(TEST_EMAIL, provider);

        // Then
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(refreshToken)
                .getBody();

        String tokenType = claims.get("token_type", String.class);
        assertThat(tokenType).isEqualTo(TokenType.REFRESH.name());
    }

    @Test
    @DisplayName("validateOAuth2Token()에서 Refresh Token 검증 시 InvalidOAuth2TokenException을 발생시켜야 한다")
    void testValidateRefreshTokenThrowsException() {
        // Given
        OAuth2Provider provider = OAuth2Provider.KAKAO;
        String refreshToken = oAuth2TokenProvider.createOAuth2RefreshToken(TEST_EMAIL, provider);

        // When & Then
        assertThatThrownBy(() -> oAuth2TokenProvider.validateOAuth2Token(refreshToken))
                .isInstanceOf(InvalidOAuth2TokenException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("validateOAuth2Token()에서 token_type claim이 없는 기존 토큰은 허용해야 한다 (과도기 지원)")
    void testValidateTokenWithoutTokenTypeClaimIsAllowed() {
        // Given - token_type claim 없는 기존 토큰 생성
        String legacyToken = Jwts.builder()
                .setSubject(PROVIDER_ID + ":" + TEST_EMAIL)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 3600000))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512,
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .setHeaderParam("type", "JWT")
                .compact();

        // When & Then - 예외가 발생하지 않아야 함
        boolean isValid = oAuth2TokenProvider.validateOAuth2Token(legacyToken);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateOAuth2Token()에서 유효한 Access Token은 true를 반환해야 한다")
    void testValidateValidAccessToken() {
        // Given
        OAuth2Provider provider = OAuth2Provider.KAKAO;
        String accessToken = oAuth2TokenProvider.createOAuth2AccessToken(TEST_EMAIL, provider);

        // When
        boolean isValid = oAuth2TokenProvider.validateOAuth2Token(accessToken);

        // Then
        assertThat(isValid).isTrue();
    }
}
