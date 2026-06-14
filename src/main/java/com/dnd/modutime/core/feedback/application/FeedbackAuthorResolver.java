package com.dnd.modutime.core.feedback.application;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.dnd.modutime.core.feedback.application.command.FeedbackAuthor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.dnd.modutime.core.common.Constants.AUTHORIZATION;
import static com.dnd.modutime.core.common.Constants.BEARER;
import static com.dnd.modutime.core.common.Constants.TOKEN_PREFIX_SEPARATOR;

/**
 * 피드백 제출자를 식별한다. Authorization 헤더가 있으면 토큰을 검증해 회원/비회원을 판정하고, 없거나 검증 실패 시 익명으로 처리한다.
 *
 * <p>피드백 경로는 {@code permitAllMatchers}에 등록되어 보안 필터가 스킵되므로, 여기서 직접 헤더를 파싱한다.
 * 만료/무효/탈퇴 유저 등으로 토큰 검증이 실패하더라도 401로 막지 않고 {@code ANONYMOUS}로 강등해
 * 피드백 제출 자체는 항상 허용한다(stale 토큰이 폼을 차단하지 않도록 하는 의도적 정책).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackAuthorResolver {

    private final OAuth2TokenProvider oAuth2TokenProvider;

    public FeedbackAuthor resolve(HttpServletRequest request) {
        String token = extractTokenOrNull(request);
        if (token == null) {
            return FeedbackAuthor.anonymous();
        }

        try {
            if (oAuth2TokenProvider.isGuestToken(token)) {
                String participantName = oAuth2TokenProvider.getOAuth2TokenClaims(token).getSubject();
                return FeedbackAuthor.guest(participantName);
            }
            if (oAuth2TokenProvider.validateOAuth2Token(token)) {
                OAuth2User principal = (OAuth2User) oAuth2TokenProvider.getAuthentication(token).getPrincipal();
                return FeedbackAuthor.member(
                        principal.user().getId(),
                        principal.user().getName(),
                        principal.user().getEmail()
                );
            }
        } catch (RuntimeException e) {
            // 만료/무효 토큰, 탈퇴 유저 등 → 피드백은 막지 않고 익명으로 강등
            log.info("피드백 제출자 토큰 검증 실패, 익명 처리: {}", e.getMessage());
        }
        return FeedbackAuthor.anonymous();
    }

    private String extractTokenOrNull(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER + TOKEN_PREFIX_SEPARATOR)) {
            return null;
        }
        String[] parts = header.split(TOKEN_PREFIX_SEPARATOR);
        if (parts.length < 2 || !StringUtils.hasText(parts[1])) {
            return null;
        }
        return parts[1];
    }
}
