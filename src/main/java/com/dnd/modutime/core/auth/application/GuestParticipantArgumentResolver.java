package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static com.dnd.modutime.core.common.Constants.AUTHORIZATION;
import static com.dnd.modutime.core.common.Constants.TOKEN_PREFIX_SEPARATOR;

/**
 * {@link GuestParticipant} 어노테이션이 붙은 파라미터에 Guest JWT 토큰의 participantName을 주입합니다.
 */
@Component
@EnableConfigurationProperties({TokenConfigurationProperties.class})
public class GuestParticipantArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String GUEST_SUBJECT_PREFIX = "guest:";

    private final TokenConfigurationProperties tokenConfigurationProperties;

    public GuestParticipantArgumentResolver(TokenConfigurationProperties tokenConfigurationProperties) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GuestParticipant.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        var request = (HttpServletRequest) webRequest.getNativeRequest();
        var token = resolveToken(request);
        return extractParticipantName(token);
    }

    private String resolveToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
        }
        return authorizationHeader.split(TOKEN_PREFIX_SEPARATOR)[1];
    }

    private String extractParticipantName(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenConfigurationProperties.secret().getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        var subject = claims.getSubject();
        if (subject == null || !subject.startsWith(GUEST_SUBJECT_PREFIX)) {
            throw new IllegalArgumentException("유효하지 않은 Guest 토큰입니다.");
        }

        // subject 형식: "guest:{roomUuid}:{participantName}"
        var parts = subject.split(":", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Guest 토큰의 subject 형식이 올바르지 않습니다.");
        }
        return parts[2];
    }
}
