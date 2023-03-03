package com.dnd.modutime.timetable.domain;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeTableFixture.getDateInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.adjustresult.application.DateTimeInfoDto;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class TimeTableTest {

    @Test
    void 참여자이름이_모두포함된_DateInfo와_TimeInfo로_DateTimeInfosDto를_반환한다() {
        TimeTable timeTable = new TimeTable(ROOM_UUID);
        timeTable.replaceDateInfos(List.of(
                getDateInfo(_2023_02_08, List.of(getTimeInfo(_12_00, List.of("김동호", "이채민")), getTimeInfo(_13_00, List.of("김동호", "이수진")))),
                getDateInfo(_2023_02_09, List.of(getTimeInfo(_12_00, List.of("김동호", "이수진")), getTimeInfo(_13_00, List.of("이채민", "이수진"))))
        ));

        List<DateTimeInfoDto> dateTimeInfosDto = timeTable.getDateTimeInfosDtoByParticipantNames(List.of("김동호", "이수진"));
        DateTimeInfoDto dateTimeInfoDto1 = dateTimeInfosDto.get(0);
        DateTimeInfoDto dateTimeInfoDto2 = dateTimeInfosDto.get(1);
        assertAll(
                () -> assertThat(dateTimeInfoDto1.getDateTime()).isEqualTo(LocalDateTime.of(_2023_02_08, _13_00)),
                () -> assertThat(dateTimeInfoDto1.getParticipantNames()).hasSize(2).contains("김동호", "이수진"),
                () -> assertThat(dateTimeInfoDto2.getDateTime()).isEqualTo(LocalDateTime.of(_2023_02_09, _12_00)),
                () -> assertThat(dateTimeInfoDto2.getParticipantNames()).hasSize(2).contains("김동호", "이수진")
        );
    }

    private TimeInfo getTimeInfo(LocalTime time, List<String> names) {
        return new TimeInfo(time, names.stream()
                .map(name -> new TimeInfoParticipantName(null, name))
                .collect(Collectors.toList()));
    }
}
