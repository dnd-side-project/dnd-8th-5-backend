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
import com.dnd.modutime.timetable.application.TimeTableService;
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
    void 방을_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    // TODO: 위치 변경 필요
    // TODO: 테스트 제대로 다시 짜야 함
    @Test
    @Disabled
    void 참여자가_가능한_시간을_교체하면_TimeBlock의_참여자를_수정한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        String roomUuid = roomCreationResponse.getUuid();
        timeTableService.create(roomUuid);
        participantService.create(roomUuid, "참여자1", "1234");

        // when
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", true, List.of(
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
