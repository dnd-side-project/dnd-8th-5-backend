package com.dnd.modutime.core.notification.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void 알림을_생성한다() {
        var notification = new Notification(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "김철수님이 가용시간을 등록했습니다.",
                "room-uuid-123",
                1L,
                "김철수"
        );

        assertThat(notification.getType()).isEqualTo(NotificationType.가용시간_등록);
        assertThat(notification.getTitle()).isEqualTo("가용시간 등록");
        assertThat(notification.getMessage()).isEqualTo("김철수님이 가용시간을 등록했습니다.");
        assertThat(notification.getRoomUuid()).isEqualTo("room-uuid-123");
        assertThat(notification.getRecipientId()).isEqualTo(1L);
        assertThat(notification.getSenderName()).isEqualTo("김철수");
        assertThat(notification.isRead()).isFalse();
        assertThat(notification.getReadAt()).isNull();
    }

    @Test
    void 알림을_읽음_처리한다() {
        var notification = new Notification(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "테스트 메시지",
                "room-uuid",
                1L,
                "김철수"
        );
        var now = LocalDateTime.of(2026, 3, 11, 14, 30, 0);

        notification.markAsRead(now);

        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isEqualTo(now);
    }

    @Test
    void 이미_읽은_알림을_다시_읽음_처리하면_변경되지_않는다() {
        var notification = new Notification(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "테스트 메시지",
                "room-uuid",
                1L,
                "김철수"
        );
        var firstReadTime = LocalDateTime.of(2026, 3, 11, 14, 0, 0);
        var secondReadTime = LocalDateTime.of(2026, 3, 11, 15, 0, 0);

        notification.markAsRead(firstReadTime);
        notification.markAsRead(secondReadTime);

        assertThat(notification.getReadAt()).isEqualTo(firstReadTime);
    }
}
