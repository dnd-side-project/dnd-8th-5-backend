package com.dnd.modutime.core.admin;

import com.dnd.modutime.core.admin.application.AdminTokenProvider;
import com.dnd.modutime.core.admin.exception.AdminTokenException;
import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import com.dnd.modutime.core.auth.security.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class AdminTokenProviderTest {

    private static final String SECRET = "A2D26455E4FB631C353E3ED6D1872";

    private final TokenConfigurationProperties properties =
            new TokenConfigurationProperties("900000", "1209600000", SECRET, false);
    private final AdminTokenProvider adminTokenProvider = new AdminTokenProvider(properties);

    @DisplayName("발급한 어드민 access token 은 검증을 통과하고 subject 로 username 을 갖는다")
    @Test
    void access_token_검증() {
        String accessToken = adminTokenProvider.createAccessToken("superadmin");

        assertThat(adminTokenProvider.validateAdminToken(accessToken)).isTrue();
        assertThat(adminTokenProvider.getUsername(accessToken)).isEqualTo("superadmin");
    }

    @DisplayName("refresh token 은 access token 검증에서 거부된다")
    @Test
    void refresh_token_은_access_검증_실패() {
        String refreshToken = adminTokenProvider.createRefreshToken("superadmin");

        assertThatThrownBy(() -> adminTokenProvider.validateAdminToken(refreshToken))
                .isInstanceOf(AdminTokenException.class);
    }

    @DisplayName("user_type 이 admin 이 아닌 토큰(예: oauth)은 어드민 검증에서 거부된다")
    @Test
    void 비_어드민_토큰_거부() {
        String oauthToken = Jwts.builder()
                .setSubject("kakao:user@example.com")
                .claim("token_type", TokenType.ACCESS.name())
                .claim("user_type", "oauth")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 900_000))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();

        assertThatThrownBy(() -> adminTokenProvider.validateAdminToken(oauthToken))
                .isInstanceOf(AdminTokenException.class);
    }

    @DisplayName("위조/손상된 토큰은 거부된다")
    @Test
    void 손상된_토큰_거부() {
        assertThatThrownBy(() -> adminTokenProvider.validateAdminToken("not-a-jwt"))
                .isInstanceOf(AdminTokenException.class);
    }

    @DisplayName("발급한 어드민 refresh token 은 refresh token 검증을 통과한다")
    @Test
    void refresh_token_검증_성공() {
        String refreshToken = adminTokenProvider.createRefreshToken("superadmin");

        assertThat(adminTokenProvider.isValidAdminRefreshToken(refreshToken)).isTrue();
    }

    @DisplayName("access token 은 refresh token 검증에서 거부된다")
    @Test
    void access_token_은_refresh_검증_실패() {
        String accessToken = adminTokenProvider.createAccessToken("superadmin");

        assertThat(adminTokenProvider.isValidAdminRefreshToken(accessToken)).isFalse();
    }

    @DisplayName("user_type 이 admin 이 아닌 refresh token 은 거부된다")
    @Test
    void 비_어드민_refresh_token_거부() {
        String oauthRefreshToken = Jwts.builder()
                .setSubject("kakao:user@example.com")
                .claim("token_type", TokenType.REFRESH.name())
                .claim("user_type", "oauth")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1_209_600_000))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();

        assertThat(adminTokenProvider.isValidAdminRefreshToken(oauthRefreshToken)).isFalse();
    }

    @DisplayName("다른 secret 으로 서명된(위조) refresh token 은 거부된다")
    @Test
    void 위조_서명_refresh_token_거부() {
        String forgedRefreshToken = Jwts.builder()
                .setSubject("superadmin")
                .claim("token_type", TokenType.REFRESH.name())
                .claim("user_type", "admin")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1_209_600_000))
                .signWith(SignatureAlgorithm.HS512, "another-secret-key-that-differs".getBytes(StandardCharsets.UTF_8))
                .compact();

        assertThat(adminTokenProvider.isValidAdminRefreshToken(forgedRefreshToken)).isFalse();
    }

    @DisplayName("형식이 깨진 refresh token 은 거부된다")
    @Test
    void 손상된_refresh_token_거부() {
        assertThat(adminTokenProvider.isValidAdminRefreshToken("not-a-jwt")).isFalse();
    }

    @DisplayName("만료되었지만 서명이 유효한 refresh token 은 통과한다(만료 판단은 DB 기준)")
    @Test
    void 만료된_refresh_token_은_서명만_유효하면_통과() {
        String expiredRefreshToken = Jwts.builder()
                .setSubject("superadmin")
                .claim("token_type", TokenType.REFRESH.name())
                .claim("user_type", "admin")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2_000))
                .setExpiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();

        assertThat(adminTokenProvider.isValidAdminRefreshToken(expiredRefreshToken)).isTrue();
    }
}
