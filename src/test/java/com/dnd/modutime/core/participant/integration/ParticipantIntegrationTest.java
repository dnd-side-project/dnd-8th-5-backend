package com.dnd.modutime.core.participant.integration;

import com.dnd.modutime.core.participant.application.ParticipantService;
import com.dnd.modutime.core.participant.domain.ParticipantRemovedEvent;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
public class ParticipantIntegrationTest {

    @Autowired
    private ParticipantService participantService;

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
        participantService.create(roomUuid, name, password);

        // when
        participantService.delete(roomUuid, name);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(participantRepository.findByRoomUuidAndName(roomUuid, name)).isEmpty();
            softly.assertThat(events.stream(ParticipantRemovedEvent.class).count()).isEqualTo(1);
        });
    }
}
