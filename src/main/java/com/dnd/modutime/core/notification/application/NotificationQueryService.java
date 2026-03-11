package com.dnd.modutime.core.notification.application;

import com.dnd.modutime.core.notification.application.response.NotificationResponse;
import com.dnd.modutime.core.notification.application.response.UnreadCountResponse;
import com.dnd.modutime.core.notification.domain.NotificationQueryRepository;
import com.dnd.modutime.infrastructure.PageRequest;
import com.dnd.modutime.infrastructure.PageResponse;
import com.dnd.modutime.util.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;
    private final TimeProvider timeProvider;

    public NotificationQueryService(NotificationQueryRepository notificationQueryRepository,
                                    TimeProvider timeProvider) {
        this.notificationQueryRepository = notificationQueryRepository;
        this.timeProvider = timeProvider;
    }

    public PageResponse<NotificationResponse> getNotifications(Long userId, PageRequest pageRequest) {
        var offset = (int) pageRequest.getOffset();
        var limit = pageRequest.getSize();
        var notifications = notificationQueryRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId, offset, limit);
        var total = notificationQueryRepository.countByRecipientId(userId);
        var content = notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        return PageResponse.of(content, pageRequest, total);
    }

    public UnreadCountResponse getUnreadCount(Long userId) {
        var count = notificationQueryRepository.countByRecipientIdAndReadFalse(userId);
        return UnreadCountResponse.of(count);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        var notification = notificationQueryRepository.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        notification.markAsRead(timeProvider.getCurrentLocalDateTime());
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationQueryRepository.markAllAsReadByRecipientId(userId);
    }
}
