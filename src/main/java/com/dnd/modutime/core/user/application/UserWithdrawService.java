package com.dnd.modutime.core.user.application;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.UserNotFoundException;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.util.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserWithdrawService {

    private final UserRepository userRepository;
    private final KakaoClient kakaoClient;
    private final UserCache userCache;
    private final TimeProvider timeProvider;

    public UserWithdrawService(final UserRepository userRepository,
                               final KakaoClient kakaoClient,
                               final UserCache userCache,
                               final TimeProvider timeProvider) {
        this.userRepository = userRepository;
        this.kakaoClient = kakaoClient;
        this.userCache = userCache;
        this.timeProvider = timeProvider;
    }

    /**
     * 회원 탈퇴.
     *
     * <ol>
     *   <li>외부 API(카카오 unlink) 먼저 호출 — 실패 시 트랜잭션 롤백되어 DB 변경 없음</li>
     *   <li>OAuth2 사용자 캐시 무효화</li>
     *   <li>User soft delete + email/oauthId/refreshToken 익명화 (UNIQUE 제약 회피)</li>
     * </ol>
     */
    @Transactional
    public void withdraw(final Long userId) {
        var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

        if (user.isWithdrawn()) {
            // @Where로 조회되지 않으므로 일반적으로 도달하지 않음 — 방어적 멱등 처리
            return;
        }

        if (user.getOauthId() == null) {
            // 백필 전 사용자 — 다시 로그인 후 시도하도록 유도
            throw new IllegalStateException("재로그인 후 다시 시도해주세요.");
        }

        if (user.getProvider() == OAuth2Provider.KAKAO) {
            this.kakaoClient.unlinkByUserId(user.getOauthId());
        }

        var cacheKey = user.getProvider().getRegistrationId() + ":" + user.getEmail();
        this.userCache.removeUserFromCache(cacheKey);

        user.withdraw(this.timeProvider.getCurrentLocalDateTime());

        log.info("user withdrawn: id={}, provider={}", user.getId(), user.getProvider());
    }
}
