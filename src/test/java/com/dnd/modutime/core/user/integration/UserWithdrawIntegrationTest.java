package com.dnd.modutime.core.user.integration;

import com.dnd.modutime.core.infrastructure.kakao.KakaoClient;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.core.user.application.UserWithdrawCommandHandler;
import com.dnd.modutime.core.user.application.UserWithdrawFacade;
import com.dnd.modutime.core.user.application.command.UserWithdrawCommand;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserCache;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("integration")
@DisplayName("회원 탈퇴 저장 통합 테스트 - 실제 HTTP/카카오 호출 없이 사유/동의 컬럼이 DB에 저장되는지 검증")
public class UserWithdrawIntegrationTest extends IntegrationSupporter {

    @Autowired
    private UserWithdrawFacade userWithdrawFacade;

    @Autowired
    private UserWithdrawCommandHandler userWithdrawCommandHandler;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KakaoClient kakaoClient;

    @MockBean
    private UserCache userCache;

    @PersistenceContext
    private EntityManager entityManager;

    // KakaoClient.unlinkByUserId / UserCache.removeUserFromCache 는 Mockito 기본 동작
    // (각각 null 반환 / no-op) 으로 충분 — 별도 stubbing 불필요.

    @Test
    @DisplayName("정상 탈퇴: withdraw_reason / withdraw_consented_at 컬럼이 채워지고 deleted_at과 동일 시각이며 익명화도 적용된다")
    void 정상_탈퇴_사유_동의시각_저장() {
        // given - 실제 user 저장
        var user = userRepository.save(
                new User("e2e-유저", "e2e@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "kakao-oauth-id-1")
        );
        Long userId = user.getId();
        String originalEmail = user.getEmail();
        String reason = "자주 사용하지 않아요";

        // when - facade 호출 (Kakao는 mocked, 실제 HTTP 안 탐)
        userWithdrawFacade.withdraw(userId, reason);

        // then - native query로 @Where 우회해 raw column 검증
        Object[] row = (Object[]) entityManager.createNativeQuery(
                "SELECT withdraw_reason, withdraw_consented_at, deleted_at, email, oauth_id, refresh_token " +
                        "FROM users WHERE id = ?"
        ).setParameter(1, userId).getSingleResult();

        String savedReason = (String) row[0];
        LocalDateTime savedConsentedAt = ((Timestamp) row[1]).toLocalDateTime();
        LocalDateTime savedDeletedAt = ((Timestamp) row[2]).toLocalDateTime();
        String savedEmail = (String) row[3];
        String savedOauthId = (String) row[4];
        String savedRefreshToken = (String) row[5];

        assertThat(savedReason).isEqualTo(reason);
        assertThat(savedConsentedAt).isNotNull().isEqualTo(savedDeletedAt);
        assertThat(savedEmail).isEqualTo("withdrawn_" + userId + "@modutime.local").isNotEqualTo(originalEmail);
        assertThat(savedOauthId).isNull();
        assertThat(savedRefreshToken).isNull();

        verify(kakaoClient, times(1)).unlinkByUserId("kakao-oauth-id-1");
        verify(userCache, times(1)).removeUserFromCache("kakao:e2e@example.com");
    }

    @Test
    @DisplayName("기타 사유: 자유서술 텍스트(라벨 + 본문)도 그대로 저장된다")
    void 기타_사유_자유서술_저장() {
        var user = userRepository.save(
                new User("e2e-기타", "etc@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "kakao-oauth-id-2")
        );
        Long userId = user.getId();
        String reason = "기타: 알림이 너무 자주 와서 부담스러웠어요. 빈도 조절 옵션이 더 세분화되면 좋겠어요.";

        userWithdrawFacade.withdraw(userId, reason);

        String savedReason = (String) entityManager.createNativeQuery(
                "SELECT withdraw_reason FROM users WHERE id = ?"
        ).setParameter(1, userId).getSingleResult();

        assertThat(savedReason).isEqualTo(reason);
    }

    @Test
    @DisplayName("DTO 검증 한계인 200자 reason도 도메인/엔티티 레벨에서 그대로 저장된다 (컬럼 길이 500이라 절단 없음)")
    void 최대길이_사유_저장() {
        var user = userRepository.save(
                new User("e2e-max", "max@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "kakao-oauth-id-3")
        );
        Long userId = user.getId();
        String reason = "가".repeat(200);

        userWithdrawFacade.withdraw(userId, reason);

        String savedReason = (String) entityManager.createNativeQuery(
                "SELECT withdraw_reason FROM users WHERE id = ?"
        ).setParameter(1, userId).getSingleResult();

        assertThat(savedReason).hasSize(200).isEqualTo(reason);
    }

    @Test
    @DisplayName("CommandHandler 직접 호출도 동일하게 사유/동의시각을 저장한다 (Facade의 외부 의존 없이 핸들러 단독 검증)")
    void 핸들러_단독_저장() {
        var user = userRepository.save(
                new User("e2e-handler", "handler@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "kakao-oauth-id-4")
        );
        Long userId = user.getId();
        String reason = "원하는 기능이 없어요";

        userWithdrawCommandHandler.handle(UserWithdrawCommand.of(userId, "kakao:handler@example.com", reason));

        Object[] row = (Object[]) entityManager.createNativeQuery(
                "SELECT withdraw_reason, withdraw_consented_at, deleted_at FROM users WHERE id = ?"
        ).setParameter(1, userId).getSingleResult();

        assertThat((String) row[0]).isEqualTo(reason);
        assertThat(row[1]).isNotNull();
        assertThat(row[2]).isNotNull();
        assertThat(((Timestamp) row[1]).toLocalDateTime()).isEqualTo(((Timestamp) row[2]).toLocalDateTime());
    }
}
