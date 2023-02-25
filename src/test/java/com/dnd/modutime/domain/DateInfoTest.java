package com.dnd.modutime.domain;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import com.dnd.modutime.domain.timeblock.AvailableTime;
import com.dnd.modutime.domain.timeblock.TimeBlock;
import com.dnd.modutime.domain.timetable.DateInfo;
import com.dnd.modutime.domain.timetable.TimeInfo;
import com.dnd.modutime.domain.timetable.TimeTable;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DateInfoTest {

    @Test
    void date가_같지_않으면_timeInfo의_count를_감소시키지_않는다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(_12_00)));
        dateInfo.minusCount(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_09,
                List.of(new AvailableTime(_12_00))));
        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getCount()).isEqualTo(1);
    }

    @Test
    void date가_같고_시간이_null이면_count를_감소시킨다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(null)));
        dateInfo.minusCount(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10, null));
        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getCount()).isEqualTo(0);
    }

    @Test
    void timeInfos에서_해당하는시간들의_count는_다_1씩_감소되어야한다() {
        DateInfo dateInfo = getDateInfo(List.of(
                getTimeInfo(_12_00),
                getTimeInfo(_13_00),
                getTimeInfo(_14_00))
        );
        dateInfo.minusCount(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_12_00),
                        new AvailableTime(_14_00))));

        List<TimeInfo> timeInfos = dateInfo.getTimeInfos();
        assertAll(
                () -> assertThat(timeInfos.get(0).getCount()).isEqualTo(0),
                () -> assertThat(timeInfos.get(1).getCount()).isEqualTo(1),
                () -> assertThat(timeInfos.get(2).getCount()).isEqualTo(0)
        );
    }

    @Test
    void timeInfos에서_해당하는시간들의_count는_다_1씩_증가되어야한다() {
        DateInfo dateInfo = getDateInfo(List.of(
                getTimeInfo(_12_00),
                getTimeInfo(_13_00),
                getTimeInfo(_14_00))
        );
        dateInfo.plusCount(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10,
                List.of(new AvailableTime(_12_00),
                        new AvailableTime(_14_00))));

        List<TimeInfo> timeInfos = dateInfo.getTimeInfos();
        assertAll(
                () -> assertThat(timeInfos.get(0).getCount()).isEqualTo(2),
                () -> assertThat(timeInfos.get(1).getCount()).isEqualTo(1),
                () -> assertThat(timeInfos.get(2).getCount()).isEqualTo(2)
        );
    }

    @Test
    void date가_같지_않으면_timeInfo의_count를_증가시키지_않는다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(_12_00)));
        dateInfo.plusCount(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_09,
                List.of(new AvailableTime(_12_00))));
        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getCount()).isEqualTo(1);
    }

    @Test
    void date가_같고_시간이_null이면_count를_증가시킨다() {
        DateInfo dateInfo = getDateInfo(List.of(getTimeInfo(null)));
        dateInfo.plusCount(new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10, null));
        TimeInfo timeInfo = dateInfo.getTimeInfos().get(0);
        assertThat(timeInfo.getCount()).isEqualTo(2);
    }

    private DateInfo getDateInfo(List<TimeInfo> timeInfos) {
        return new DateInfo(getTimeTable(), _2023_02_10, timeInfos);
    }

    private TimeTable getTimeTable() {
        return new TimeTable(ROOM_UUID);
    }

    private TimeInfo getTimeInfo(LocalTime localTime) {
        return new TimeInfo(localTime, 1);
    }
}
