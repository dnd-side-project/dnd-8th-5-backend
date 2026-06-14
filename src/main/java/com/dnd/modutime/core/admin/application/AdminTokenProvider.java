package com.dnd.modutime.core.admin.application;

import com.dnd.modutime.core.admin.exception.AdminTokenException;
import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import com.dnd.modutime.core.auth.security.TokenType;
import com.dnd.modutime.core.common.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 어드민 전용 JWT 발급/검증.
 *
 * <p>기존 OAuth2/Guest 토큰과 동일한 비밀키(HS512)를 사용하되, {@code user_type=admin} 클레임으로
 * 토큰 주체를 구분한다. 따라서 OAuth/게스트 토큰은 어드민 엔드포인트에서, 어드민 토큰은 서비스
 * 엔드포인트에서 서로 통과되지 않는다. subject 는 어드민 username 이다.</p>
 */
@Slf4j
@Component
@EnableConfigurationProperties({TokenConfigurationProperties.class})
public class AdminTokenProvider {

    private static final String USER_TYPE_ADMIN = "admin";
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_USER_TYPE = "user_type";

    private final TokenConfigurationProperties tokenConfigurationProperties;

    public AdminTokenProvider(final TokenConfigurationProperties tokenConfigurationProperties) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
    }

    public String createAccessToken(final String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_TOKEN_TYPE, TokenType.ACCESS.name())
                .claim(CLAIM_USER_TYPE, USER_TYPE_ADMIN)
                .setIssuedAt(new Date())
                .setExpiration(createAccessTokenExpireTime())
                .signWith(SignatureAlgorithm.HS512, secret())
                .setHeaderParam("type", "JWT")
                .compact();
    }

    public String createRefreshToken(final String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_TOKEN_TYPE, TokenType.REFRESH.name())
                .claim(CLAIM_USER_TYPE, USER_TYPE_ADMIN)
                .setIssuedAt(new Date())
                .setExpiration(createRefreshTokenExpireTime())
                .signWith(SignatureAlgorithm.HS512, secret())
                .setHeaderParam("type", "JWT")
                .compact();
    }

    public Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(tokenConfigurationProperties.accessTokenExpirationTime()));
    }

    public Date createRefreshTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(tokenConfigurationProperties.refreshTokenExpirationTime()));
    }

    /**
     * 어드민 access token 인지 검증한다.
     *
     * @throws AdminTokenException 만료/위조/타입 불일치(어드민 아님, access 아님) 시
     */
    public boolean validateAdminToken(final String token) {
        Claims claims = parseClaims(token);

        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!TokenType.ACCESS.name().equals(tokenType)) {
            throw new AdminTokenException("Admin Access Token이 아닙니다.", ErrorCode.INVALID_TOKEN);
        }

        String userType = claims.get(CLAIM_USER_TYPE, String.class);
        if (!USER_TYPE_ADMIN.equals(userType)) {
            throw new AdminTokenException("Admin 토큰이 아닙니다.", ErrorCode.INVALID_TOKEN);
        }

        return true;
    }

    public String getUsername(final String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(final String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("어드민 토큰이 만료되었습니다.");
            throw new AdminTokenException("토큰이 만료되었습니다.", ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (AdminTokenException e) {
            throw e;
        } catch (Exception e) {
            log.warn("유효하지 않은 어드민 토큰입니다.");
            throw new AdminTokenException("해당 토큰은 유효한 토큰이 아닙니다.", ErrorCode.INVALID_TOKEN);
        }
    }

    private byte[] secret() {
        return tokenConfigurationProperties.secret().getBytes(StandardCharsets.UTF_8);
    }
}
