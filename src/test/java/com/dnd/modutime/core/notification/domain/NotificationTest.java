package com.dnd.modutime.core.notification.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void 알림을_생성한다() {
        var data = Map.of(
                "roomUuid", "room-uuid-123",
                "participantName", "김철수"
        );
        var notification = Notification.of(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "김철수님이 가용시간을 등록했습니다.",
                1L,
                data
        );

        assertThat(notification.getType()).isEqualTo(NotificationType.가용시간_등록);
        assertThat(notification.getTitle()).isEqualTo("가용시간 등록");
        assertThat(notification.getMessage()).isEqualTo("김철수님이 가용시간을 등록했습니다.");
        assertThat(notification.getRecipientId()).isEqualTo(1L);
        assertThat(notification.getData()).containsEntry("roomUuid", "room-uuid-123");
        assertThat(notification.getData()).containsEntry("participantName", "김철수");
        assertThat(notification.isRead()).isFalse();
        assertThat(notification.getReadAt()).isNull();
    }

    @Test
    void 알림을_읽음_처리한다() {
        var notification = Notification.of(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "테스트 메시지",
                1L,
                Map.of()
        );
        var now = LocalDateTime.of(2026, 3, 11, 14, 30, 0);

        notification.markAsRead(now);

        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isEqualTo(now);
    }

    @Test
    void 알림을_발송_완료_처리한다() {
        var notification = Notification.of(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "테스트 메시지",
                1L,
                Map.of()
        );
        var now = LocalDateTime.of(2026, 3, 11, 14, 30, 0);

        notification.markAsSent(now);

        assertThat(notification.isSent()).isTrue();
        assertThat(notification.getSentAt()).isEqualTo(now);
    }

    @Test
    void 알림_생성시_sent는_false이다() {
        var notification = Notification.of(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "테스트 메시지",
                1L,
                Map.of()
        );

        assertThat(notification.isSent()).isFalse();
        assertThat(notification.getSentAt()).isNull();
    }

    @Test
    void 이미_읽은_알림을_다시_읽음_처리하면_변경되지_않는다() {
        var notification = Notification.of(
                NotificationType.가용시간_등록,
                "가용시간 등록",
                "테스트 메시지",
                1L,
                Map.of()
        );
        var firstReadTime = LocalDateTime.of(2026, 3, 11, 14, 0, 0);
        var secondReadTime = LocalDateTime.of(2026, 3, 11, 15, 0, 0);

        notification.markAsRead(firstReadTime);
        notification.markAsRead(secondReadTime);

        assertThat(notification.getReadAt()).isEqualTo(firstReadTime);
    }
}
