package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import com.dnd.modutime.core.auth.security.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@EnableConfigurationProperties({TokenConfigurationProperties.class})
public class GuestTokenProvider {

    private final TokenConfigurationProperties tokenConfigurationProperties;

    public GuestTokenProvider(final TokenConfigurationProperties tokenConfigurationProperties) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
    }

    public String createAccessToken(final String roomUuid, final String participantName) {
        return Jwts.builder()
                .setSubject("guest:" + roomUuid + ":" + participantName)
                .claim("token_type", TokenType.ACCESS.name())
                .setIssuedAt(new Date())
                .setExpiration(createAccessTokenExpireTime())
                .signWith(SignatureAlgorithm.HS512, tokenConfigurationProperties.secret().getBytes(StandardCharsets.UTF_8))
                .setHeaderParam("type", "JWT")
                .compact();
    }

    public Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(tokenConfigurationProperties.accessTokenExpirationTime()));
    }
}
