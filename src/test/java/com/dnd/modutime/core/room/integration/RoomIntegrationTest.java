package com.dnd.modutime.core.room.integration;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timetable.application.response.AvailableTimeInfo;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.timetable.application.response.TimeAndCountPerDate;
import com.dnd.modutime.core.timetable.application.response.TimeTableResponse;
import com.dnd.modutime.core.participant.application.ParticipantService;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.core.timetable.application.TimeTableService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@Import(TimeConfiguration.class)
@RecordApplicationEvents
@SpringBootTest
public class RoomIntegrationTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private ParticipantService participantService;

    @MockBean
    private TimeReplaceValidator timeReplaceValidator;

    @Autowired
    private TimeBlockService timeBlockService;

    @Autowired
    private ApplicationEvents events;

    @Test
    void ??????_????????????() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    // TODO: ?????? ?????? ??????
    // TODO: ????????? ????????? ?????? ?????? ???
    @Test
    @Disabled
    void ????????????_?????????_?????????_????????????_TimeBlock???_????????????_????????????() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        String roomUuid = roomCreationResponse.getUuid();
        timeTableService.create(roomUuid);
        participantService.create(roomUuid, "?????????1", "1234");

        // when
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("?????????1", true, List.of(
                LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));
        timeBlockService.replace(roomUuid, timeReplaceRequest);

        // then
        TimeTableResponse timeTable = timeTableService.getTimeTable(roomUuid);
        List<TimeAndCountPerDate> timeAndCountPerDates = timeTable.getTimeAndCountPerDates();
        TimeAndCountPerDate timeAndCountPerDate = timeAndCountPerDates.get(0);
        AvailableTimeInfo _12_00_timeAndCount = timeAndCountPerDate.getAvailableTimeInfos().get(2);
        AvailableTimeInfo _13_00_timeAndCount = timeAndCountPerDate.getAvailableTimeInfos().get(4);

        assertAll(
                () -> assertThat(events.stream(TimeBlockReplaceEvent.class).count()).isEqualTo(1),
                () -> assertThat(_12_00_timeAndCount.getCount()).isEqualTo(1),
                () -> assertThat(_13_00_timeAndCount.getCount()).isEqualTo(1)
        );
    }
}
