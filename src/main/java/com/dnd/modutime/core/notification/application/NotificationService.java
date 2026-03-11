package com.dnd.modutime.core.notification.application;

import com.dnd.modutime.core.notification.domain.DeviceToken;
import com.dnd.modutime.core.notification.domain.DeviceTokenQueryRepository;
import com.dnd.modutime.core.notification.domain.Notification;
import com.dnd.modutime.core.notification.domain.NotificationQueryRepository;
import com.dnd.modutime.core.notification.domain.NotificationRepository;
import com.dnd.modutime.core.notification.domain.NotificationSender;
import com.dnd.modutime.core.notification.domain.NotificationType;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantQueryRepository;
import com.dnd.modutime.core.room.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final ParticipantQueryRepository participantQueryRepository;
    private final DeviceTokenQueryRepository deviceTokenQueryRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;
    private final RoomRepository roomRepository;

    public NotificationService(ParticipantQueryRepository participantQueryRepository,
                               DeviceTokenQueryRepository deviceTokenQueryRepository,
                               NotificationRepository notificationRepository,
                               NotificationSender notificationSender,
                               RoomRepository roomRepository) {
        this.participantQueryRepository = participantQueryRepository;
        this.deviceTokenQueryRepository = deviceTokenQueryRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void 가용시간_등록_알림(String roomUuid, String participantName) {
        var participants = participantQueryRepository.findByRoomUuid(roomUuid);

        var targetUserIds = participants.stream()
                .filter(p -> !p.getName().equals(participantName))
                .filter(Participant::isRegisteredUser)
                .map(Participant::getUserId)
                .collect(Collectors.toList());

        if (targetUserIds.isEmpty()) {
            return;
        }

        var room = roomRepository.findByUuid(roomUuid).orElse(null);
        var roomTitle = room != null ? room.getTitle() : "";
        var title = "가용시간 등록";
        var message = participantName + "님이 가용시간을 등록했습니다.";

        // 알림 이력 저장
        var notifications = targetUserIds.stream()
                .map(userId -> new Notification(
                        NotificationType.가용시간_등록,
                        title,
                        message,
                        roomUuid,
                        userId,
                        participantName
                ))
                .collect(Collectors.toList());
        notificationRepository.saveAll(notifications);

        // FCM 푸시 발송
        var deviceTokens = deviceTokenQueryRepository.findByUserIdIn(targetUserIds);
        var tokens = deviceTokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("type", "AVAILABILITY_SUBMITTED");
        data.put("roomUuid", roomUuid);
        data.put("roomTitle", roomTitle);
        data.put("participantName", participantName);
        data.put("message", message);

        notificationSender.send(tokens, title, message, data);
    }
}
