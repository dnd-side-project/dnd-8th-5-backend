package com.dnd.modutime.core.admin;

import com.dnd.modutime.core.admin.domain.Admin;
import com.dnd.modutime.util.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class AdminTest {

    @DisplayName("비밀번호가 일치하면 true 를 반환한다")
    @Test
    void 비밀번호_일치() {
        Admin admin = new Admin("superadmin", "pw1234");

        assertThat(admin.matchesPassword("pw1234")).isTrue();
    }

    @DisplayName("비밀번호가 일치하지 않으면 false 를 반환한다")
    @Test
    void 비밀번호_불일치() {
        Admin admin = new Admin("superadmin", "pw1234");

        assertThat(admin.matchesPassword("wrong")).isFalse();
    }

    @DisplayName("refresh token 이 없으면 만료된 것으로 간주한다")
    @Test
    void refresh_없으면_만료() {
        Admin admin = new Admin("superadmin", "pw1234");

        assertThat(admin.isRefreshTokenExpired()).isTrue();
    }

    @DisplayName("refresh token 만료 시각이 미래면 만료되지 않은 것으로 본다")
    @Test
    void refresh_미래_만료시각() {
        Admin admin = new Admin("superadmin", "pw1234");
        admin.updateRefreshToken("refresh-token", DateTimeUtils.currentUTC().plusDays(14));

        assertThat(admin.isRefreshTokenExpired()).isFalse();
    }

    @DisplayName("refresh token 만료 시각이 과거면 만료된 것으로 본다")
    @Test
    void refresh_과거_만료시각() {
        Admin admin = new Admin("superadmin", "pw1234");
        admin.updateRefreshToken("refresh-token", DateTimeUtils.currentUTC().minusSeconds(1));

        assertThat(admin.isRefreshTokenExpired()).isTrue();
    }
}
