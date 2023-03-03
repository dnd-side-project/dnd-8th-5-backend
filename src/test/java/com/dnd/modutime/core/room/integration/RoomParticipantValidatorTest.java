package com.dnd.modutime.core.room.integration;

import static com.dnd.modutime.fixture.RoomFixture.getRoomByHeadCount;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import com.dnd.modutime.core.room.application.RoomParticipantValidator;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RoomParticipantValidatorTest {

    @Autowired
    private RoomParticipantValidator roomParticipantValidator;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    void headCount가_없을경우_예외가_발생하지_않는다() {
        Room room = getRoomByHeadCount(null);
        Room savedRoom = roomRepository.save(room);
        assertDoesNotThrow(() -> roomParticipantValidator.validate(savedRoom.getUuid()));
    }

    @Test
    void headCount가_방의_참여자의_수가_같을경우_예외가_발생한다() {
        Room room = getRoomByHeadCount(3);
        Room savedRoom = roomRepository.save(room);
        세명을_저장한다(savedRoom.getUuid());
        assertThatThrownBy(() -> roomParticipantValidator.validate(savedRoom.getUuid()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void 세명을_저장한다(String roomUUid) {
        participantRepository.save(new Participant(roomUUid, "김동호", "1234"));
        participantRepository.save(new Participant(roomUUid, "이수진", "1234"));
        participantRepository.save(new Participant(roomUUid, "이세희", "1234"));
    }
}
