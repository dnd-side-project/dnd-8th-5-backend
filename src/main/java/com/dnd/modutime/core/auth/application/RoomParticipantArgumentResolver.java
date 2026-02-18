package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.dnd.modutime.core.common.Constants.AUTHORIZATION;
import static com.dnd.modutime.core.common.Constants.TOKEN_PREFIX_SEPARATOR;

/**
 * {@link RoomParticipant} 어노테이션이 붙은 파라미터에 {@link ParticipantInfo}를 주입합니다.
 * SecurityContext에 OAuth2User가 있으면 OAuth로, 없으면 JWT에서 Guest 정보를 추출합니다.
 */
@Component
@EnableConfigurationProperties({TokenConfigurationProperties.class})
public class RoomParticipantArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String GUEST_SUBJECT_PREFIX = "guest:";

    private final TokenConfigurationProperties tokenConfigurationProperties;

    public RoomParticipantArgumentResolver(TokenConfigurationProperties tokenConfigurationProperties) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RoomParticipant.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        var request = (HttpServletRequest) webRequest.getNativeRequest();

        // 1. SecurityContext에서 OAuth2User 확인
        var oAuth2User = resolveOAuth2User();
        if (oAuth2User != null) {
            return new ParticipantInfo(
                    ParticipantType.OAUTH,
                    null,
                    oAuth2User.user().getName(),
                    oAuth2User.user().getId()
            );
        }

        // 2. Guest JWT 토큰 파싱
        var token = resolveToken(request);
        var participantInfo = extractGuestParticipantInfo(token);

        // 3. Guest인 경우 roomUuid path variable 일치 검증
        var roomUuidPathVariable = parameter.getParameterAnnotation(RoomParticipant.class).roomPathVariable();
        if (!roomUuidPathVariable.isEmpty()) {
            validateRoomUuid(request, participantInfo, roomUuidPathVariable);
        }

        return participantInfo;
    }

    private OAuth2User resolveOAuth2User() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User;
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
        }
        return authorizationHeader.split(TOKEN_PREFIX_SEPARATOR)[1];
    }

    private ParticipantInfo extractGuestParticipantInfo(String token) {
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
        return new ParticipantInfo(ParticipantType.GUEST, parts[1], parts[2], null);
    }

    @SuppressWarnings("unchecked")
    private void validateRoomUuid(HttpServletRequest request, ParticipantInfo participantInfo, String roomUuidPathVariable) {
        var pathVariables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables == null) {
            return;
        }
        var pathRoomUuid = pathVariables.get(roomUuidPathVariable);
        if (pathRoomUuid != null && !pathRoomUuid.equals(participantInfo.roomUuid())) {
            throw new IllegalArgumentException("토큰의 roomUuid와 요청 경로의 roomUuid가 일치하지 않습니다.");
        }
    }
}
