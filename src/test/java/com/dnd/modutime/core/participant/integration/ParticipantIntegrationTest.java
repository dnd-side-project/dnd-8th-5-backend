package com.dnd.modutime.core.participant.integration;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.participant.application.ParticipantCommandHandler;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.core.participant.domain.ParticipantRemovedEvent;
import com.dnd.modutime.util.IntegrationSupporter;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.List;

@SpringBootTestWithoutOAuthConfig
@RecordApplicationEvents
public class ParticipantIntegrationTest extends IntegrationSupporter {

    @Autowired
    private ParticipantCommandHandler participantCommandHandler;

    @Autowired
    private ParticipantFacade participantFacade;

    @Autowired
    private ParticipantQueryService participantQueryService;

    @Autowired
    private ApplicationEvents events;

    @DisplayName("참여자를 삭제한다.")
    @Test
    void test01() {
        // given
        var roomUuid = "roomUuid";
        var name = "name";
        var password = "1234";
        var participant1 = participantCommandHandler.handle(ParticipantCreateCommand.of(roomUuid, name, password));
        var participant2 = participantCommandHandler.handle(ParticipantCreateCommand.of(roomUuid, "name2", password));

        // when
        var command = ParticipantsDeleteCommand.of(roomUuid, List.of(participant1.getId(), participant2.getId()));
        participantFacade.delete(command);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(participantQueryService.getByRoomUuidAndName(roomUuid, name)).isEmpty();
            softly.assertThat(events.stream(ParticipantRemovedEvent.class).count()).isEqualTo(2);
        });
    }
}
