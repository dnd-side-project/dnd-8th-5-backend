package com.dnd.modutime.core.notification.application;

import com.dnd.modutime.core.notification.domain.NotificationType;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantQueryRepository;
import com.dnd.modutime.core.room.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final ParticipantQueryRepository participantQueryRepository;
    private final RoomRepository roomRepository;
    private final NotificationProcessor notificationProcessor;

    public NotificationService(ParticipantQueryRepository participantQueryRepository,
                               RoomRepository roomRepository,
                               NotificationProcessor notificationProcessor) {
        this.participantQueryRepository = participantQueryRepository;
        this.roomRepository = roomRepository;
        this.notificationProcessor = notificationProcessor;
    }

    @Transactional
    public void sendReplaceMessage(String roomUuid, String participantName) {
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

        var data = new HashMap<String, String>();
        data.put("type", "AVAILABILITY_SUBMITTED");
        data.put("roomUuid", roomUuid);
        data.put("roomTitle", roomTitle);
        data.put("participantName", participantName);
        data.put("message", message);

        notificationProcessor.process(
                targetUserIds,
                NotificationType.가용시간_등록,
                title,
                message,
                data
        );
    }
}
