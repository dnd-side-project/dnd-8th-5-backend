package com.dnd.modutime.timetable.integration;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.participant.application.ParticipantService;
import com.dnd.modutime.timeblock.application.TimeBlockService;
import com.dnd.modutime.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.timetable.application.TimeTableUpdateService;
import com.dnd.modutime.timetable.domain.DateInfo;
import com.dnd.modutime.timetable.domain.TimeInfo;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.timetable.domain.TimeTableParticipantName;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
import java.util.List;
import java.util.stream.Collectors;
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

    @Autowired
    private TimeTableUpdateService timeTableUpdateService;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private ApplicationEvents events;

    @Test
    void 참여자가_가능한_시간을_교체하면_TimeTable의_참여자를_수정한다() {
        participantService.create(ROOM_UUID, "참여자1", "1234");
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);
        TimeTable timeTable = timeTableRepository.findByRoomUuid(ROOM_UUID).get();
        DateInfo dateInfo = timeTable.getDateInfos().get(0);
        TimeInfo timeInfo_12_00 = dateInfo.getTimeInfos().get(0);
        TimeInfo timeInfo_13_00 = dateInfo.getTimeInfos().get(1);

        assertAll(
                () -> assertThat(events.stream(TimeBlockReplaceEvent.class).count()).isEqualTo(1),
                () -> assertThat(timeInfo_12_00.getTimeTableParticipantNames().stream()
                        .map(TimeTableParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(1)
                        .contains("참여자1"),
                () -> assertThat(timeInfo_13_00.getTimeTableParticipantNames().stream()
                        .map(TimeTableParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(1)
                        .contains("참여자1")
        );
    }
}
