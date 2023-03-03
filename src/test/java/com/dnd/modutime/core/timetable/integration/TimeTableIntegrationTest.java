package com.dnd.modutime.core.timetable.integration;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.participant.application.ParticipantService;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.core.timetable.application.TimeTableUpdateService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
public class TimeTableIntegrationTest {

    @MockBean
    private TimeReplaceValidator timeReplaceValidator;

    @Autowired
    private TimeBlockService timeBlockService;

    @Autowired
    private ParticipantService participantService;

    @MockBean
    private TimeTableUpdateService timeTableUpdateService;

    @Autowired
    private ApplicationEvents events;

    @Test
    void 참여자가_가능한_시간을_교체하면_TimeTable을_수정한다() {
        participantService.create(ROOM_UUID, "참여자1", "1234");
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", true, List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);

        assertAll(
                () -> assertThat(events.stream(TimeBlockReplaceEvent.class).count()).isEqualTo(1),
                () -> verify(timeTableUpdateService).update(any())
        );
    }
}
