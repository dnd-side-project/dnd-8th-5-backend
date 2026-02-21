package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.auth.oauth.facade.TokenConfigurationProperties;
import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.exception.InvalidPasswordException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
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
 * 두 경우 모두 DB 조회를 통해 해당 방의 참여자인지 검증합니다.
 */
@Component
@EnableConfigurationProperties({TokenConfigurationProperties.class})
public class RoomParticipantArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenConfigurationProperties tokenConfigurationProperties;
    private final ParticipantQueryService participantQueryService;
    private final UserRepository userRepository;

    public RoomParticipantArgumentResolver(TokenConfigurationProperties tokenConfigurationProperties,
                                           ParticipantQueryService participantQueryService,
                                           UserRepository userRepository) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
        this.participantQueryService = participantQueryService;
        this.userRepository = userRepository;
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
        var annotation = parameter.getParameterAnnotation(RoomParticipant.class);
        if (annotation == null) {
            throw new IllegalStateException("@RoomParticipant 어노테이션을 찾을 수 없습니다.");
        }
        var roomUuid = extractRoomUuid(request, annotation.roomPathVariable());

        // 1. SecurityContext에서 OAuth2User 확인
        var oAuth2User = resolveOAuth2User();
        if (oAuth2User != null) {
            return resolveOAuthParticipant(oAuth2User, roomUuid);
        }

        // 2. Guest JWT 토큰 파싱 및 DB 검증
        return resolveGuestParticipant(request, roomUuid);
    }

    private ParticipantInfo resolveOAuthParticipant(OAuth2User oAuth2User, String roomUuid) {
        var email = oAuth2User.user().getEmail();
        var provider = oAuth2User.getProvider();
        var user = userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(() -> new IllegalArgumentException("해당 OAuth 사용자를 찾을 수 없습니다."));

        var participant = participantQueryService.findByRoomUuidAndUserId(roomUuid, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 방에 참여하지 않은 사용자입니다."));

        return new ParticipantInfo(ParticipantType.OAUTH, roomUuid, participant.getName(), user.getId());
    }

    private ParticipantInfo resolveGuestParticipant(HttpServletRequest request, String roomUuid) {
        var token = resolveToken(request);
        var claims = parseGuestClaims(token);

        var name = claims.getSubject();
        var password = claims.get("password", String.class);

        var participant = participantQueryService.getByRoomUuidAndName(roomUuid, name)
                .orElseThrow(InvalidPasswordException::new);

        if (!participant.matchPassword(password)) {
            throw new InvalidPasswordException();
        }

        return new ParticipantInfo(ParticipantType.GUEST, roomUuid, name, null);
    }

    @SuppressWarnings("unchecked")
    private String extractRoomUuid(HttpServletRequest request, String roomUuidPathVariable) {
        var pathVariables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables == null || !pathVariables.containsKey(roomUuidPathVariable)) {
            throw new IllegalArgumentException("roomUuid path variable을 찾을 수 없습니다.");
        }
        return pathVariables.get(roomUuidPathVariable);
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

    private Claims parseGuestClaims(String token) {
        return Jwts.parser()
                .setSigningKey(tokenConfigurationProperties.secret().getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
}
