package com.dnd.modutime.core.participant.integration;

import com.dnd.modutime.core.participant.application.ParticipantService;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import com.dnd.modutime.core.timeblock.application.ParticipantCreationEvent;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.Optional;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RecordApplicationEvents
public class ParticipantIntegrationTest extends IntegrationSupporter {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Autowired
    private ApplicationEvents events;

    @Test
    void 참여자가_생성되면_참여자의_TimeBlock이_생성된다() {
        participantService.create(ROOM_UUID, "참여자1", "1234");
        Optional<TimeBlock> actual = timeBlockRepository.findByRoomUuidAndParticipantName(ROOM_UUID, "참여자1");
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(events.stream(ParticipantCreationEvent.class).count()).isEqualTo(1)
        );
    }
}
