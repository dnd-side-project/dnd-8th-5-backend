package com.dnd.modutime.core.notification.application;

import com.dnd.modutime.core.notification.domain.*;
import com.dnd.modutime.util.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NotificationProcessor {

    private final NotificationSender notificationSender;
    private final NotificationRepository notificationRepository;
    private final DeviceTokenQueryRepository deviceTokenQueryRepository;
    private final TimeProvider timeProvider;

    public NotificationProcessor(NotificationSender notificationSender,
                                 NotificationRepository notificationRepository,
                                 DeviceTokenQueryRepository deviceTokenQueryRepository,
                                 TimeProvider timeProvider) {
        this.notificationSender = notificationSender;
        this.notificationRepository = notificationRepository;
        this.deviceTokenQueryRepository = deviceTokenQueryRepository;
        this.timeProvider = timeProvider;
    }

    public void process(List<Long> targetUserIds,
                        NotificationType type,
                        String title,
                        String message,
                        Map<String, String> data) {
        // 1. 알림 엔티티 생성
        var notifications = targetUserIds.stream()
                .map(userId -> Notification.of(type, title, message, userId, data))
                .collect(Collectors.toList());

        // 2. 대상 유저의 디바이스 토큰 조회
        var deviceTokens = deviceTokenQueryRepository.findByUserIdIn(targetUserIds);
        var tokens = deviceTokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        // 3. 토큰이 있으면 FCM 발송 후 결과 반영
        if (!tokens.isEmpty()) {
            var result = notificationSender.send(tokens, title, message, data);
            if (result.hasSuccess()) {
                var now = timeProvider.getCurrentLocalDateTime();
                notifications.forEach(n -> n.markAsSent(now));
            }
        }

        // 4. 이력 저장 (발송 결과가 반영된 상태로)
        notificationRepository.saveAll(notifications);
    }
}
