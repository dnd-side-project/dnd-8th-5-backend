package com.dnd.modutime.core.notification.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceTokenTest {

    @Test
    void 디바이스_토큰을_생성한다() {
        var deviceToken = new DeviceToken("test-fcm-token", 1L);

        assertThat(deviceToken.getToken()).isEqualTo("test-fcm-token");
        assertThat(deviceToken.getUserId()).isEqualTo(1L);
    }

    @Test
    void 토큰이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DeviceToken(null, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 토큰이_빈문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DeviceToken("", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void userId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DeviceToken("test-token", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
