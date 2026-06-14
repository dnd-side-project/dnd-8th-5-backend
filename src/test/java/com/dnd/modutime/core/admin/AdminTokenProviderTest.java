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
}
