package com.dnd.modutime.core.timeblock.application;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.participant.application.ParticipantCommandHandler;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.core.participant.domain.ParticipantRemovedEvent;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.domain.TimeBlockRemovedEvent;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.List;
import java.util.Optional;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

@SpringBootTestWithoutOAuthConfig
@RecordApplicationEvents
class TimeBlockEventHandlerTest extends IntegrationSupporter {

    @Autowired
    private ParticipantCommandHandler participantCommandHandler;

    @Autowired
    private ParticipantFacade participantFacade;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @SpyBean
    private TimeBlockService timeBlockService;

    @Autowired
    private ApplicationEvents events;

    @DisplayName("참여자가 생성되면 참여자의 타임블록 생성이 호출된다.")
    @Test
    void 참여자가_생성되면_참여자의_TimeBlock이_생성된다() {
        var participantName = "참여자1";
        var command = ParticipantCreateCommand.of(ROOM_UUID, participantName, "1234");
        participantCommandHandler.handle(command);
        Optional<TimeBlock> actual = timeBlockRepository.findByRoomUuidAndParticipantName(ROOM_UUID, participantName);
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(events.stream(ParticipantCreationEvent.class).count()).isEqualTo(1),
                () -> verify(timeBlockService).create(ROOM_UUID, participantName)
        );
    }

    @DisplayName("참여자가 삭제되면 타임블록도 삭제가 호출된다.")
    @Test
    void test01() {
        var participantName = "참여자1";
        var participant = participantCommandHandler.handle(ParticipantCreateCommand.of(ROOM_UUID, participantName, "1234"));

        // when
        var command = ParticipantsDeleteCommand.of(ROOM_UUID, List.of(participant.getId()));
        participantFacade.delete(command);

        // then
        assertAll(
                () -> assertThat(events.stream(ParticipantRemovedEvent.class).count()).isEqualTo(1),
                () -> assertThat(events.stream(TimeBlockRemovedEvent.class).count()).isEqualTo(1),
                () -> verify(timeBlockService).remove(ROOM_UUID, participantName)
        );
    }
}
