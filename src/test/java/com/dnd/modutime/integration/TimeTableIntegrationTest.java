package com.dnd.modutime.integration;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.dnd.modutime.application.ParticipantService;
import com.dnd.modutime.application.TimeBlockService;
import com.dnd.modutime.application.TimeReplaceValidator;
import com.dnd.modutime.application.TimeTableUpdateService;
import com.dnd.modutime.domain.timeblock.TimeBlockReplaceEvent;
import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
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
    void 참여자가_가능한_시간을_교체하면_TimeTable의_count를_수정한다() {
        participantService.create(ROOM_UUID, "참여자1", "1234");
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);

        assertAll(
                () -> assertThat(events.stream(TimeBlockReplaceEvent.class).count()).isEqualTo(1),
                () -> verify(timeTableUpdateService).update(any())
        );
    }
}
