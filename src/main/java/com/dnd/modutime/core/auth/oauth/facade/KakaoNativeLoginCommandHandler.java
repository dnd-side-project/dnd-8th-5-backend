package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.OAuth2UserResolver;
import com.dnd.modutime.core.auth.oauth.facade.command.KakaoNativeLoginCommand;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 네이티브 카카오 로그인의 DB/캐시 mutation 단계.
 *
 * <p>외부 API 호출(KakaoClient.getUserInfo)은 {@link KakaoNativeAuthFacade}에서 수행하고,
 * 본 핸들러는 사용자 조회/생성, 캐시 적재, JWT 발급, refresh token 저장만 책임진다.
 * UserWithdrawCommandHandler 와 동일한 짧은-트랜잭션 패턴.</p>
 */
@Slf4j
@Service
public class KakaoNativeLoginCommandHandler {

    private final OAuth2UserResolver oAuth2UserResolver;
    private final OAuth2TokenProvider oAuth2TokenProvider;
    private final OAuth2TokenService oAuth2TokenService;

    public KakaoNativeLoginCommandHandler(final OAuth2UserResolver oAuth2UserResolver,
                                          final OAuth2TokenProvider oAuth2TokenProvider,
                                          final OAuth2TokenService oAuth2TokenService) {
        this.oAuth2UserResolver = oAuth2UserResolver;
        this.oAuth2TokenProvider = oAuth2TokenProvider;
        this.oAuth2TokenService = oAuth2TokenService;
    }

    @Transactional
    public KakaoNativeLoginResult handle(final KakaoNativeLoginCommand command) {
        var oAuth2User = this.oAuth2UserResolver.resolveAndCache(
                command.getUserDetails(),
                command.getRawAttributes(),
                "id"
        );

        User user = oAuth2User.user();
        if (user.isWithdrawn()) {
            // @Where(deleted_at IS NULL) 로 보통 자동 차단되지만 캐시 hit 시를 대비한 방어 분기.
            throw new AuthenticationException("탈퇴한 사용자입니다.", ErrorCode.USER_NOT_FOUND);
        }

        var tokens = this.oAuth2TokenProvider.createOAuth2JwtTokenResponse(user.getEmail(), user.getProvider());
        this.oAuth2TokenService.saveOrUpdateOAuth2RefreshToken(user.getEmail(), user.getProvider(), tokens);

        return new KakaoNativeLoginResult(tokens, user);
    }
}
