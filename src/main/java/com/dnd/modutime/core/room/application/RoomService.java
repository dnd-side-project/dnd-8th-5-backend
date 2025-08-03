package com.dnd.modutime.core.room.application;

import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.room.application.request.TimerRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.RoomInfoResponse;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.util.TimeProvider;
import com.dnd.modutime.util.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final TimeProvider timeProvider;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    public RoomCreationResponse create(RoomRequest roomRequest) {
        TimerRequest timerRequest = roomRequest.getTimerRequest();
        Room room = new Room(
                roomRequest.getTitle(),
                roomRequest.getStartTime(),
                roomRequest.getEndTime(),
                roomRequest.getDates().stream()
                        .map(RoomDate::new)
                        .collect(Collectors.toList()),
                roomRequest.getHeadCount(),
                findDeadLineOrNull(timerRequest),
                timeProvider);
        roomRepository.save(room);

        return new RoomCreationResponse(room.getUuid());
    }

    private LocalDateTime findDeadLineOrNull(TimerRequest timerRequest) {
        if (hasDeadLine(timerRequest)) {
            return null;
        }
        return Timer.calculateDeadLine(timerRequest.getDay(),
                timerRequest.getHour(),
                timerRequest.getMinute(),
                timeProvider);
    }

    private boolean hasDeadLine(TimerRequest timerRequest) {
        return timerRequest == null || checkAllValueZero(timerRequest);
    }

    private boolean checkAllValueZero(TimerRequest timerRequest) {
        return timerRequest.getDay() == 0
               && timerRequest.getHour() == 0
               && timerRequest.getMinute() == 0;
    }

    public String getTitleByUuid(String roomUuid) {
        Room room = getByUuid(roomUuid);
        return room.getTitle();
    }

    public RoomInfoResponse getInfo(String roomUuid) {
        Room room = getByUuid(roomUuid);
        List<Participant> participants = participantRepository.findByRoomUuid(roomUuid);
        List<LocalDate> roomDates = room.getRoomDates().stream()
                .map(RoomDate::getDate)
                .collect(Collectors.toList());
        return new RoomInfoResponse(room.getTitle(),
                room.getDeadLineOrNull(),
                room.getHeadCountOrNull(),
                participants.stream()
                        .map(Participant::getName)
                        .collect(Collectors.toList()),
                roomDates.stream()
                        .sorted()
                        .collect(Collectors.toList()),
                room.getStartTimeOrNull(),
                room.getEndTimeOrNull());
    }

    private Room getByUuid(String roomUuid) {
        return roomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
