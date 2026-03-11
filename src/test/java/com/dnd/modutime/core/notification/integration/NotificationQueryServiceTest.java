package com.dnd.modutime.core.notification.integration;

import com.dnd.modutime.core.notification.application.NotificationQueryService;
import com.dnd.modutime.core.notification.domain.Notification;
import com.dnd.modutime.core.notification.domain.NotificationRepository;
import com.dnd.modutime.core.notification.domain.NotificationType;
import com.dnd.modutime.infrastructure.PageRequest;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class NotificationQueryServiceTest extends IntegrationSupporter {

    @Autowired
    private NotificationQueryService notificationQueryService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void 알림_목록을_페이징_조회한다() {
        // given
        var userId = 1L;
        notificationRepository.save(createNotification(userId, "메시지1"));
        notificationRepository.save(createNotification(userId, "메시지2"));
        notificationRepository.save(createNotification(userId, "메시지3"));

        // when
        var result = notificationQueryService.getNotifications(userId, PageRequest.of(0, 2));

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(3);
    }

    @Test
    void 읽지_않은_알림_수를_조회한다() {
        // given
        var userId = 1L;
        notificationRepository.save(createNotification(userId, "메시지1"));
        notificationRepository.save(createNotification(userId, "메시지2"));

        // when
        var result = notificationQueryService.getUnreadCount(userId);

        // then
        assertThat(result.count()).isEqualTo(2);
    }

    @Test
    void 알림을_읽음_처리한다() {
        // given
        var userId = 1L;
        var notification = notificationRepository.save(createNotification(userId, "테스트"));

        // when
        notificationQueryService.markAsRead(notification.getId(), userId);

        // then
        var unreadCount = notificationQueryService.getUnreadCount(userId);
        assertThat(unreadCount.count()).isEqualTo(0);
    }

    @Test
    void 다른_사용자의_알림은_읽음_처리할_수_없다() {
        // given
        var ownerId = 1L;
        var otherId = 2L;
        var notification = notificationRepository.save(createNotification(ownerId, "테스트"));

        // when & then
        assertThatThrownBy(() -> notificationQueryService.markAsRead(notification.getId(), otherId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 알림입니다");
    }

    @Test
    void 전체_알림을_읽음_처리한다() {
        // given
        var userId = 1L;
        notificationRepository.save(createNotification(userId, "메시지1"));
        notificationRepository.save(createNotification(userId, "메시지2"));

        // when
        notificationQueryService.markAllAsRead(userId);

        // then
        var unreadCount = notificationQueryService.getUnreadCount(userId);
        assertThat(unreadCount.count()).isEqualTo(0);
    }

    private Notification createNotification(Long recipientId, String message) {
        return Notification.of(
                NotificationType.AVAILABILITY_REGISTERED,
                "가용시간 등록",
                message,
                recipientId,
                Map.of("roomUuid", "room-uuid", "participantName", "김철수")
        );
    }
}
