package com.dnd.modutime.core.timeblock.application;

import com.dnd.modutime.core.participant.application.ParticipantService;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.domain.TimeBlockRemovedEvent;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.Optional;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@RecordApplicationEvents
class TimeBlockEventHandlerTest {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Autowired
    private ApplicationEvents events;

    @DisplayName("참여자가 생성되면 참여자의 TimeBlock이 생성된다")
    @Test
    void 참여자가_생성되면_참여자의_TimeBlock이_생성된다() {
        participantService.create(ROOM_UUID, "참여자1", "1234");
        Optional<TimeBlock> actual = timeBlockRepository.findByRoomUuidAndParticipantName(ROOM_UUID, "참여자1");
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(events.stream(ParticipantCreationEvent.class).count()).isEqualTo(1)
        );
    }

    @DisplayName("참여자가 삭제되면 타임블록도 삭제된다.")
    @Test
    void test01() {
        var participantName = "참여자1";
        participantService.create(ROOM_UUID, participantName, "1234");

        // when
        participantService.delete(ROOM_UUID, participantName);

        // then
        assertThat(events.stream(TimeBlockRemovedEvent.class).count()).isEqualTo(1);
    }
}
