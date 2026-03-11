package com.dnd.modutime.core.notification.integration;

import com.dnd.modutime.core.notification.application.NotificationService;
import com.dnd.modutime.core.notification.domain.NotificationQueryRepository;
import com.dnd.modutime.core.notification.domain.NotificationSender;
import com.dnd.modutime.core.notification.domain.DeviceToken;
import com.dnd.modutime.core.notification.domain.DeviceTokenRepository;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantRepository;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.util.IntegrationSupporter;
import com.dnd.modutime.util.TimeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Transactional
public class NotificationServiceTest extends IntegrationSupporter {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired
    private NotificationQueryRepository notificationQueryRepository;

    @Autowired
    private TimeProvider timeProvider;

    @MockBean
    private NotificationSender notificationSender;

    @Test
    void 가용시간_등록시_같은_방의_다른_참여자에게_알림을_발송한다() {
        // given
        var room = createRoom("팀 회의");
        var sender = Participant.of(room.getUuid(), "김철수", 1L);
        var receiver = Participant.of(room.getUuid(), "이영희", 2L);
        participantRepository.save(sender);
        participantRepository.save(receiver);
        deviceTokenRepository.save(new DeviceToken("receiver-fcm-token", 2L));

        // when
        notificationService.가용시간_등록_알림(room.getUuid(), "김철수");

        // then
        verify(notificationSender).send(anyList(), anyString(), anyString(), any());
    }

    @Test
    void 가용시간_등록시_알림_이력이_저장된다() {
        // given
        var room = createRoom("팀 회의");
        var sender = Participant.of(room.getUuid(), "김철수", 1L);
        var receiver = Participant.of(room.getUuid(), "이영희", 2L);
        participantRepository.save(sender);
        participantRepository.save(receiver);
        deviceTokenRepository.save(new DeviceToken("receiver-fcm-token", 2L));

        // when
        notificationService.가용시간_등록_알림(room.getUuid(), "김철수");

        // then
        var notifications = notificationQueryRepository
                .findByRecipientIdOrderByCreatedAtDesc(2L, 0, 10);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getMessage()).contains("김철수");
        assertThat(notifications.get(0).getRecipientId()).isEqualTo(2L);
    }

    @Test
    void OAuth_사용자가_아닌_참여자에게는_알림을_보내지_않는다() {
        // given
        var room = createRoom("팀 회의");
        var sender = Participant.of(room.getUuid(), "김철수", 1L);
        var guestParticipant = new Participant(room.getUuid(), "게스트유저", "1234");
        participantRepository.save(sender);
        participantRepository.save(guestParticipant);

        // when
        notificationService.가용시간_등록_알림(room.getUuid(), "김철수");

        // then
        verify(notificationSender, never()).send(anyList(), anyString(), anyString(), any());
    }

    @Test
    void 본인에게는_알림을_보내지_않는다() {
        // given
        var room = createRoom("팀 회의");
        var sender = Participant.of(room.getUuid(), "김철수", 1L);
        participantRepository.save(sender);
        deviceTokenRepository.save(new DeviceToken("sender-fcm-token", 1L));

        // when
        notificationService.가용시간_등록_알림(room.getUuid(), "김철수");

        // then
        verify(notificationSender, never()).send(anyList(), anyString(), anyString(), any());
    }

    @Test
    void 디바이스_토큰이_없는_사용자에게는_푸시를_보내지_않는다() {
        // given
        var room = createRoom("팀 회의");
        var sender = Participant.of(room.getUuid(), "김철수", 1L);
        var receiver = Participant.of(room.getUuid(), "이영희", 2L);
        participantRepository.save(sender);
        participantRepository.save(receiver);
        // 디바이스 토큰 등록 안 함

        // when
        notificationService.가용시간_등록_알림(room.getUuid(), "김철수");

        // then
        verify(notificationSender, never()).send(anyList(), anyString(), anyString(), any());
        // 하지만 알림 이력은 저장됨
        var notifications = notificationQueryRepository
                .findByRecipientIdOrderByCreatedAtDesc(2L, 0, 10);
        assertThat(notifications).hasSize(1);
    }

    private Room createRoom(String title) {
        var roomDate = new RoomDate(LocalDate.of(2026, 3, 15));
        var room = new Room(title, null, null, List.of(roomDate), null, null, timeProvider);
        return roomRepository.save(room);
    }
}
