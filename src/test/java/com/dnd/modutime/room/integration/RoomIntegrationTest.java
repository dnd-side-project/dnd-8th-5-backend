package com.dnd.modutime.room.integration;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.dto.response.AvailableTimeInfo;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import com.dnd.modutime.dto.response.TimeAndCountPerDate;
import com.dnd.modutime.dto.response.TimeTableResponse;
import com.dnd.modutime.participant.application.ParticipantService;
import com.dnd.modutime.room.application.RoomService;
import com.dnd.modutime.timeblock.application.TimeBlockService;
import com.dnd.modutime.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.timetable.application.RoomCreationEvent;
import com.dnd.modutime.timetable.application.TimeTableService;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
import java.util.List;
import java.util.Optional;
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
    private TimeTableRepository timeTableRepository;

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
    void 방을_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    @Test
    void 방이_생성되면_TimeTable이_초기화된다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);

        Optional<TimeTable> actual = timeTableRepository.findByRoomUuid(roomCreationResponse.getUuid());
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(events.stream(RoomCreationEvent.class).count()).isEqualTo(1)
        );
    }

    // TODO: 위치 변경 필요
    @Test
    void 참여자가_가능한_시간을_교체하면_TimeBlock의_참여자를_수정한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        String roomUuid = roomCreationResponse.getUuid();
        participantService.create(roomUuid, "참여자1", "1234");

        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));
        timeBlockService.replace(roomUuid, timeReplaceRequest);

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
