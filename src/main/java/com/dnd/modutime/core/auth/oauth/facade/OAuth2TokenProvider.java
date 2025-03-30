package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse;
import com.dnd.modutime.core.auth.oauth.dto.OAuth2JwtSubject;
import com.dnd.modutime.core.auth.oauth.exception.ExpiredOAuth2TokenException;
import com.dnd.modutime.core.auth.oauth.exception.InvalidOAuth2TokenException;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class OAuth2TokenProvider {

    @Value("${token.access-token-expiration-time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh-token-expiration-time}")
    private String refreshTokenExpirationTime;

    @Value("${token.secret}")
    private String tokenSecret;

    private final UserRepository userRepository;
    private final UserCache userCache;

    public OAuth2TokenProvider(final UserRepository userRepository, final UserCache userCache) {
        this.userRepository = userRepository;
        this.userCache = userCache;
    }

    public JwtTokenResponse createOAuth2JwtTokenResponse(final String email, final OAuth2Provider provider) {
        Date oAuth2accessTokenExpireTime = createAccessTokenExpireTime();
        Date oAuth2refreshTokenExpireTime = createRefreshTokenExpireTime();
        String oAuth2AccessToken = createOAuth2AccessToken(email, provider);
        String oAuth2RefreshToken = createOAuth2RefreshToken(email, provider);

        return new JwtTokenResponse(
                oAuth2AccessToken,
                oAuth2accessTokenExpireTime,
                oAuth2RefreshToken,
                oAuth2refreshTokenExpireTime
        );
    }

    public Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime));
    }

    public Date createRefreshTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
    }

    public String createOAuth2AccessToken(final String email, final OAuth2Provider provider) {
        return Jwts.builder()
                .setSubject(provider.getRegistrationId() + ":" + email)
                // .claim(KEY_ROLE, authorities) TODO :: 권한 정책 추가시 설정 필요
                .setIssuedAt(new Date())
                .setExpiration(createAccessTokenExpireTime())
                .signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes(StandardCharsets.UTF_8))
                .setHeaderParam("type", "JWT")
                .compact();
    }

    public String createOAuth2RefreshToken(final String email, final OAuth2Provider provider) {
        return Jwts.builder()
                .setSubject(provider.getRegistrationId() + ":" + email)
                .setIssuedAt(new Date())
                .setExpiration(createRefreshTokenExpireTime())
                .signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes(StandardCharsets.UTF_8))
                .setHeaderParam("type", "JWT")
                .compact();
    }

    public boolean validateOAuth2Token(final String oAuth2AccessToken) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(oAuth2AccessToken);

            return true;
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다.");
            throw new ExpiredOAuth2TokenException("토큰이 만료되었습니다.", ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.warn("유효하지 않은 토큰입니다.");
            throw new InvalidOAuth2TokenException("해당 토큰은 유효한 토큰이 아닙니다.", ErrorCode.INVALID_TOKEN);
        }
    }

    public Authentication getAuthentication(final String token) {
        Claims claims = getOAuth2TokenClaims(token);

        String subject = claims.getSubject();
        UserDetails userDetails = this.userCache.getUserFromCache(subject);
        log.debug("UserDetails from cache: {}", userDetails);

        if (userDetails != null) {
            return new OAuth2AuthenticationToken(
                    (OAuth2User) userDetails,
                    Collections.emptyList(),
                    ((OAuth2User) userDetails).getProvider().getRegistrationId()
            );
        }

        return loadAuthentication(token);
    }

    public Authentication loadAuthentication(final String token) {
        Claims claims = getOAuth2TokenClaims(token);

        String subject = claims.getSubject();
        OAuth2JwtSubject parsedOAuth2JwtSubject = new OAuth2JwtSubject(subject);

        String email = parsedOAuth2JwtSubject.getEmail();
        String registrationId = parsedOAuth2JwtSubject.getRegistrationId();

        OAuth2Provider provider = OAuth2Provider.findByRegistrationId(registrationId);

        User user = userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow();

        OAuth2User principal = new OAuth2User(
                new User(user.getName(),
                        email,
                        user.getProfileImage(),
                        user.getThumbnailImage(),
                        provider
                ),
                claims,
                "sub"
        );

        return new OAuth2AuthenticationToken(principal, Collections.emptyList(), registrationId);
    }

    public Claims getOAuth2TokenClaims(final String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다.");
            throw new ExpiredOAuth2TokenException("토큰이 만료되었습니다.", ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.info("유효하지 않은 토큰입니다.");
            throw new InvalidOAuth2TokenException("해당 토큰은 유효한 토큰이 아닙니다.", ErrorCode.INVALID_TOKEN);
        }
    }
}
