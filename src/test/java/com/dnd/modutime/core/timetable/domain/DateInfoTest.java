package com.dnd.modutime.core.timetable.domain;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static com.dnd.modutime.fixture.TimeTableFixture.getAvailableDateTime;
import static com.dnd.modutime.fixture.TimeTableFixture.getDateInfo;
import static com.dnd.modutime.fixture.TimeTableFixture.getTimeInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfoParticipantName;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class DateInfoTest {

    @Test
    void date가_같지_않으면_timeInfo의_참여자를_지우지_않는다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(_12_00)));
        dateInfo.addParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_12_00))), "참여자1");

        dateInfo.removeParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_09,
                List.of(new AvailableTime(_12_00))), "참여자1");
        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).contains("참여자1");
    }

    @Test
    void date가_같고_시간이_null이면_참여자를_지운다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(null)));
        dateInfo.addParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                null), "참여자1");

        dateInfo.removeParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                null), "참여자1");
        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).doesNotContain("참여자1");
    }

    @Test
    void timeInfos에서_해당하는시간들의_참여자는_다_지워져야한다() {
        DateInfo dateInfo = getDateInfo(List.of(
                getTimeInfo(_12_00),
                getTimeInfo(_13_00),
                getTimeInfo(_14_00))
        );
        dateInfo.addParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_12_00))), "참여자1");
        dateInfo.addParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_13_00))), "참여자1");
        dateInfo.addParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_14_00))), "참여자1");

        dateInfo.removeParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_12_00),
                        new AvailableTime(_14_00))), "참여자1");

        List<TimeInfo> timeInfos = dateInfo.getTimeInfos();
        assertAll(
                () -> assertThat(timeInfos.get(0).getTimeInfoParticipantNames()).hasSize(0),
                () -> assertThat(timeInfos.get(1).getTimeInfoParticipantNames()).hasSize(1),
                () -> assertThat(timeInfos.get(2).getTimeInfoParticipantNames()).hasSize(0)
        );
    }

    @Test
    void timeInfos에서_해당하는_시간들의_참여자로_추가한다() {
        DateInfo dateInfo = getDateInfo(List.of(
                getTimeInfo(_12_00),
                getTimeInfo(_13_00),
                getTimeInfo(_14_00))
        );

        dateInfo.addParticipantNameIfSameDate(getAvailableDateTime("참여자1", _2023_02_10,
                List.of(new AvailableTime(_12_00), new AvailableTime(_14_00))), "참여자1");

        List<TimeInfo> timeInfos = dateInfo.getTimeInfos();
        assertAll(
                () -> assertThat(timeInfos.get(0).getTimeInfoParticipantNames()).hasSize(1),
                () -> assertThat(timeInfos.get(1).getTimeInfoParticipantNames()).hasSize(0),
                () -> assertThat(timeInfos.get(2).getTimeInfoParticipantNames()).hasSize(1)
        );
    }

    @Test
    void date가_같지_않으면_timeInfo의_참여자를_추가하지_않는다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(_12_00)));

        dateInfo.addParticipantNameIfSameDate(getAvailableDateTime("참여자1", _2023_02_09,
                List.of(new AvailableTime(_12_00))), "참여자1");

        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getTimeInfoParticipantNames()).hasSize(0);
    }

    @Test
    void date가_같고_시간이_null이면_참여자를_추가한다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(null)));
        dateInfo.addParticipantNameIfSameDate(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                null), "참여자1");

        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).contains("참여자1");
    }

//    private DateInfo getDateInfo(List<TimeInfo> timeInfos) {
//        return new DateInfo(getTimeTable(), _2023_02_10, timeInfos);
//    }
//
//    private TimeTable getTimeTable() {
//        return new TimeTable(ROOM_UUID);
//    }
//
//    private TimeInfo getTimeInfo(LocalTime time) {
//        return new TimeInfo(time, new ArrayList<>());
//    }
//
//    private AvailableDateTime getAvailableDateTime(String participantName, LocalDate date, List<AvailableTime> availableTimes) {
//        return new AvailableDateTime(new TimeBlock(ROOM_UUID, participantName), date, availableTimes);
//    }
}
