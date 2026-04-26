package com.dnd.modutime.core.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("User")
class UserTest {

    @Test
    @DisplayName("5-arg 생성자는 oauthId가 null로 시작한다")
    void 기존생성자_oauthId_null() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO);

        assertThat(user.getOauthId()).isNull();
    }

    @Test
    @DisplayName("6-arg 생성자는 oauthId를 세팅한다")
    void 새생성자_oauthId_세팅() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "12345");

        assertThat(user.getOauthId()).isEqualTo("12345");
    }

    @Test
    @DisplayName("linkOAuthIdIfAbsent는 oauthId가 null일 때만 채운다")
    void linkOAuthIdIfAbsent_백필() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO);
        assertThat(user.getOauthId()).isNull();

        user.linkOAuthIdIfAbsent("12345");

        assertThat(user.getOauthId()).isEqualTo("12345");
    }

    @Test
    @DisplayName("linkOAuthIdIfAbsent는 이미 값이 있으면 덮어쓰지 않는다")
    void linkOAuthIdIfAbsent_기존값_보존() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "original");

        user.linkOAuthIdIfAbsent("new-value");

        assertThat(user.getOauthId()).isEqualTo("original");
    }

    @Test
    @DisplayName("linkOAuthIdIfAbsent에 null을 넘기면 변경하지 않는다")
    void linkOAuthIdIfAbsent_null_입력_무시() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO);

        user.linkOAuthIdIfAbsent(null);

        assertThat(user.getOauthId()).isNull();
    }

    @Test
    @DisplayName("withdraw 호출 시 deletedAt이 채워지고 email/oauthId/refreshToken이 익명화된다")
    void withdraw_익명화() {
        var user = new User("이름", "original@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "12345");
        user.updateRefreshToken("refresh-token", LocalDateTime.now().plusDays(14));
        var now = LocalDateTime.of(2026, 4, 26, 10, 0);

        user.withdraw(now);

        assertThat(user.isWithdrawn()).isTrue();
        assertThat(user.getDeletedAt()).isEqualTo(now);
        assertThat(user.getEmail()).startsWith("withdrawn_").endsWith("@modutime.local");
        assertThat(user.getEmail()).isNotEqualTo("original@example.com");
        assertThat(user.getOauthId()).isNull();
        assertThat(user.getRefreshToken()).isNull();
        assertThat(user.getTokenExpirationTime()).isEqualTo(now);
    }

    @Test
    @DisplayName("isWithdrawn은 탈퇴 전에는 false")
    void isWithdrawn_탈퇴전() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO);

        assertThat(user.isWithdrawn()).isFalse();
    }

    @Test
    @DisplayName("withdraw에 null을 넘기면 NPE")
    void withdraw_null_NPE() {
        var user = new User("이름", "test@example.com", "p.jpg", "t.jpg", OAuth2Provider.KAKAO, "12345");

        assertThatThrownBy(() -> user.withdraw(null))
                .isInstanceOf(NullPointerException.class);
    }
}
