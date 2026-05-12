package com.dnd.modutime.core.auth.oauth.facade;

import com.dnd.modutime.core.auth.oauth.OAuth2UserDetails;
import com.dnd.modutime.core.auth.oauth.controller.dto.KakaoNativeLoginResponse;
import com.dnd.modutime.core.auth.oauth.exception.KakaoEmailNotProvidedException;
import com.dnd.modutime.core.auth.oauth.facade.command.KakaoNativeLoginCommand;
import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 네이티브 앱 카카오 로그인 진입점.
 *
 * <p>외부 카카오 API 호출(KakaoClient.getUserInfo)을 트랜잭션 외부에서 수행하고,
 * 카카오 응답 지연이 DB 커넥션 점유로 이어지지 않도록 DB/캐시 mutation 은
 * {@link KakaoNativeLoginCommandHandler}(@Transactional)에 위임한다.
 * UserWithdrawFacade 와 동일한 패턴.</p>
 */
@Slf4j
@Service
public class KakaoNativeAuthFacade {

    private final KakaoClient kakaoClient;
    private final ObjectMapper objectMapper;
    private final KakaoNativeLoginCommandHandler commandHandler;

    public KakaoNativeAuthFacade(final KakaoClient kakaoClient,
                                 final ObjectMapper objectMapper,
                                 final KakaoNativeLoginCommandHandler commandHandler) {
        this.kakaoClient = kakaoClient;
        this.objectMapper = objectMapper;
        this.commandHandler = commandHandler;
    }

    public KakaoNativeLoginResponse login(final KakaoNativeLoginCommand command) {
        // 1) 외부 API 호출 — DB 트랜잭션 외부
        Map<String, Object> attributes = this.kakaoClient.getUserInfo(command.getKakaoAccessToken());

        // 2) 파싱/검증 — 부수효과 없음
        OAuth2UserDetails details = OAuth2UserDetails.of(
                OAuth2Provider.KAKAO.getRegistrationId(),
                attributes,
                this.objectMapper
        );
        if (details.email() == null) {
            throw new KakaoEmailNotProvidedException("카카오 계정 이메일 제공에 동의해야 로그인할 수 있습니다.");
        }

        // 3) DB/캐시 mutation 위임 — 별도 짧은 트랜잭션
        KakaoNativeLoginResult result = this.commandHandler.handle(command.bindUserDetails(details, attributes));

        log.info("native kakao login: provider=KAKAO, userId={}", result.user().getId());

        return KakaoNativeLoginResponse.of(result.tokens(), result.user(), command.getRoomUuid());
    }
}
