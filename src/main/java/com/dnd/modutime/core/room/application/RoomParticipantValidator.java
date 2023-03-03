package com.dnd.modutime.core.room.application;

import com.dnd.modutime.core.participant.application.ParticipantCreateValidator;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomParticipantValidator implements ParticipantCreateValidator {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public void validate(String roomUuid) {
        Room room = getRoomByRoomUuid(roomUuid);
        List<Participant> participants = participantRepository.findByRoomUuid(roomUuid);
        Integer headCountOrNull = room.getHeadCountOrNull();
        if (headCountOrNull != null && headCountOrNull == participants.size()) {
            throw new IllegalArgumentException("방의 참여자를 초과해 참여자를 추가할 수 없습니다.");
        }
    }

    private Room getRoomByRoomUuid(String roomUuid) {
        return roomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
