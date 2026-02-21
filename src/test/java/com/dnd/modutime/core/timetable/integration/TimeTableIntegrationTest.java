package com.dnd.modutime.core.timetable.integration;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timetable.application.TimeTableInitializer;
import com.dnd.modutime.core.timetable.application.TimeTableService;
import com.dnd.modutime.core.timetable.application.command.TimeTableUpdateCommand;
import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfoParticipantName;
import com.dnd.modutime.core.timetable.domain.TimeTableReplaceEvent;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTestWithoutOAuthConfig
@Transactional
@RecordApplicationEvents
public class TimeTableIntegrationTest extends IntegrationSupporter {

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private TimeBlockService timeBlockService;

    @MockBean
    private TimeTableInitializer timeTableInitializer;

    @Autowired
    private ApplicationEvents events;

    @DisplayName("타임테이블을 생성한다.")
    @Test
    void test01() {
        // given
        var roomUuid = "room-uuid";
        doNothing().when(timeTableInitializer).initialize(any(), any());

        // when
        timeTableService.create(roomUuid);

        // then
        var actual = timeTableRepository.findByRoomUuid(roomUuid);
        assertThat(actual.isPresent()).isTrue();
    }

    @DisplayName("타임 테이블의 참여자를 수정한다.")
    @Test
    void test02() {
        // given
        var roomUuid = "room-uuid";
        timeTableService.create(roomUuid);
        var timeTable = timeTableRepository.findByRoomUuid(roomUuid).get();
        List<DateInfo> dateInfos = new ArrayList<>();
        List<TimeInfo> timeInfos1 = new ArrayList<>();
        timeInfos1.add(new TimeInfo(_00_00, new ArrayList<>()));
        timeInfos1.add(new TimeInfo(_00_30, new ArrayList<>()));
        List<TimeInfo> timeInfos2 = new ArrayList<>();
        timeInfos2.add(new TimeInfo(_00_00, new ArrayList<>()));
        timeInfos2.add(new TimeInfo(_00_30, new ArrayList<>()));
        dateInfos.add(new DateInfo(timeTable, _2023_02_09, timeInfos1));
        dateInfos.add(new DateInfo(timeTable, _2023_02_10, timeInfos2));
        timeTable.replaceDateInfos(dateInfos);
        var participantName = "참여자1";
        var oldAvailableDateTime = new AvailableDateTime(new TimeBlock(ROOM_UUID, participantName), _2023_02_09, List.of(new AvailableTime(_00_00)));
        timeTableService.update(TimeTableUpdateCommand.of(
                roomUuid,
                List.of(),
                List.of(oldAvailableDateTime),
                participantName
        ));
        events.clear();

        // when
        var newAvailableDateTime = new AvailableDateTime(new TimeBlock(ROOM_UUID, participantName), _2023_02_10, List.of(new AvailableTime(_00_00), new AvailableTime(_00_30)));
        var command = TimeTableUpdateCommand.of(
                roomUuid,
                List.of(oldAvailableDateTime),
                List.of(newAvailableDateTime),
                participantName
        );
        timeTableService.update(command);

        // then
        var actualDateInfos = timeTable.getDateInfos();
        var _02_09_dateInfo = actualDateInfos.get(0);
        var _02_09_dateInfo_participants = _02_09_dateInfo.getTimeInfos().stream()
                .flatMap(timeInfo -> timeInfo.getTimeInfoParticipantNames().stream())
                .map(TimeInfoParticipantName::getName)  // TimeInfoParticipantName에서 이름 추출
                .collect(Collectors.toList());
        var _02_10_dateInfo = actualDateInfos.get(1);
        var _02_10_dateInfo_participants = _02_10_dateInfo.getTimeInfos().stream()
                .flatMap(timeInfo -> timeInfo.getTimeInfoParticipantNames().stream())
                .map(TimeInfoParticipantName::getName)  // TimeInfoParticipantName에서 이름 추출
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(actualDateInfos.size()).isEqualTo(2),
                () -> assertThat(_02_09_dateInfo.getDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09_dateInfo.getTimeInfos().size()).isEqualTo(2),
                () -> assertThat(_02_09_dateInfo_participants).isEmpty(),
                () -> assertThat(_02_10_dateInfo.getDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10_dateInfo.getTimeInfos().size()).isEqualTo(2),
                () -> assertThat(_02_10_dateInfo_participants.size()).isEqualTo(2),
                () -> assertThat(_02_10_dateInfo_participants).containsExactly(participantName, participantName),
                () -> assertThat(events.stream(TimeTableReplaceEvent.class).count()).isEqualTo(1)
        );
    }
}
