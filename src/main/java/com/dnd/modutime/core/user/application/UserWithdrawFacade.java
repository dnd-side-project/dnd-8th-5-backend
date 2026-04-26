package com.dnd.modutime.core.user.application;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.user.InsufficientAuthenticationException;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.core.user.application.command.UserWithdrawCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserWithdrawFacade {

    private final UserRepository userRepository;
    private final KakaoClient kakaoClient;
    private final UserWithdrawCommandHandler commandHandler;

    public UserWithdrawFacade(final UserRepository userRepository,
                              final KakaoClient kakaoClient,
                              final UserWithdrawCommandHandler commandHandler) {
        this.userRepository = userRepository;
        this.kakaoClient = kakaoClient;
        this.commandHandler = commandHandler;
    }

    /**
     * 회원 탈퇴.
     *
     * <ol>
     *   <li>사용자 조회 — User의 @Where(deleted_at IS NULL)로 인해 탈퇴자는 UserNotFoundException(404)</li>
     *   <li>외부 API(카카오 unlink) — DB 트랜잭션 외부에서 호출하여 카카오 응답 지연이 DB 커넥션 점유로 이어지지 않도록 함</li>
     *   <li>CommandHandler에 위임 — 별도 짧은 트랜잭션에서 캐시 무효화 + soft delete</li>
     * </ol>
     *
     * <p>Unlink 성공 후 DB 업데이트 실패 시에도, 카카오는 이미 unlink 상태이고
     * KakaoResponseHandler가 -101(이미 연결 해제됨)을 정상 처리하므로 재시도 안전.</p>
     */
    public void withdraw(final Long userId, final String reason) {
        var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

        if (user.getOauthId() == null) {
            throw new InsufficientAuthenticationException(
                    "재로그인 후 다시 시도해주세요.",
                    ErrorCode.INSUFFICIENT_AUTHENTICATION
            );
        }

        if (user.getProvider() == OAuth2Provider.KAKAO) {
            this.kakaoClient.unlinkByUserId(user.getOauthId());
        }

        var cacheKey = user.getProvider().getRegistrationId() + ":" + user.getEmail();
        this.commandHandler.handle(UserWithdrawCommand.of(user.getId(), cacheKey, reason));

        log.info("user withdrawn: id={}, provider={}", user.getId(), user.getProvider());
    }
}
