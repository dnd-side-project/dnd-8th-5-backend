package com.dnd.modutime.core.participant.integration;

import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.core.participant.domain.ParticipantRemovedEvent;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.List;

@SpringBootTest
@RecordApplicationEvents
public class ParticipantIntegrationTest {

    @Autowired
    private ParticipantFacade participantFacade;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ApplicationEvents events;

    @DisplayName("참여자를 삭제한다.")
    @Test
    void test01() {
        // given
        var roomUuid = "roomUuid";
        var name = "name";
        var password = "1234";
        participantFacade.create(roomUuid, name, password);
        participantFacade.create(roomUuid, "name2", password);

        // when
        var command = ParticipantsDeleteCommand.of(roomUuid, List.of(name, "name2"));
        participantFacade.delete(command);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(participantRepository.findByRoomUuidAndName(roomUuid, name)).isEmpty();
            softly.assertThat(events.stream(ParticipantRemovedEvent.class).count()).isEqualTo(2);
        });
    }
}
