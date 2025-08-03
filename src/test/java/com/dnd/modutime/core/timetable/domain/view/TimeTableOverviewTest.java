package com.dnd.modutime.core.timetable.domain.view;

import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.util.JsonUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TimeTableOverviewTest {

    @Test
    void participantNames가_null이면_모든_TimeInfo를_포함한_TimeTableOverview를_반환한다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호", "이수진"));
        var timeInfo2 = createTimeInfo(_13_00, List.of("이채민"));
        var dateInfo = createDateInfo(_2023_02_08, List.of(timeInfo1, timeInfo2));
        timeTable.replaceDateInfos(List.of(dateInfo));

        // when
        var overview = TimeTableOverview.from(timeTable, null);

        // then
        var timeAndCountPerDates = overview.getTimeAndCountPerDates();
        assertAll(
                () -> assertThat(timeAndCountPerDates).hasSize(1),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableDate()).isEqualTo(_2023_02_08),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos()).hasSize(2),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(0).getTime()).isEqualTo(_12_00),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(0).getCount()).isEqualTo(2),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(1).getTime()).isEqualTo(_13_00),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(1).getCount()).isEqualTo(1)
        );
    }

    @Test
    void participantNames가_빈_리스트면_모든_TimeInfo를_포함한_TimeTableOverview를_반환한다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호", "이수진"));
        var timeInfo2 = createTimeInfo(_13_00, List.of("이채민"));
        var dateInfo = createDateInfo(_2023_02_08, List.of(timeInfo1, timeInfo2));
        timeTable.replaceDateInfos(List.of(dateInfo));

        // when
        var overview = TimeTableOverview.from(timeTable, List.of());

        // then
        var timeAndCountPerDates = overview.getTimeAndCountPerDates();
        assertAll(
                () -> assertThat(timeAndCountPerDates).hasSize(1),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableDate()).isEqualTo(_2023_02_08),
                () -> assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos()).hasSize(2)
        );
    }

    @Test
    void 특정_participantNames가_있으면_해당_참여자가_포함된_TimeInfo만_포함한_TimeTableOverview를_반환한다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호", "이수진"));
        var timeInfo2 = createTimeInfo(_13_00, List.of("이채민"));
        var timeInfo3 = createTimeInfo(_14_00, List.of("김동호"));
        var dateInfo = createDateInfo(_2023_02_08, List.of(timeInfo1, timeInfo2, timeInfo3));
        timeTable.replaceDateInfos(List.of(dateInfo));

        // when
        var overview = TimeTableOverview.from(timeTable, List.of("김동호"));

        // then
        var timeAndCountPerDates = overview.getTimeAndCountPerDates();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(timeAndCountPerDates).hasSize(1);
            softly.assertThat(timeAndCountPerDates.get(0).getAvailableDate()).isEqualTo(_2023_02_08);
            softly.assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos()).hasSize(2);
            softly.assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(0).getTime()).isEqualTo(_12_00);
            softly.assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(0).getCount()).isEqualTo(1);
            softly.assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(1).getTime()).isEqualTo(_14_00);
            softly.assertThat(timeAndCountPerDates.get(0).getAvailableTimeInfos().get(1).getCount()).isEqualTo(1);
        });
    }

    @Test
    void 여러_participantNames가_있으면_해당_참여자들_중_하나라도_포함된_TimeInfo만_포함한_TimeTableOverview를_반환한다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호", "이수진"));
        var timeInfo2 = createTimeInfo(_13_00, List.of("이채민"));
        var timeInfo3 = createTimeInfo(_14_00, List.of("박민수"));
        var dateInfo = createDateInfo(_2023_02_08, List.of(timeInfo1, timeInfo2, timeInfo3));
        timeTable.replaceDateInfos(List.of(dateInfo));

        // when
        var overview = TimeTableOverview.from(timeTable, List.of("김동호", "이채민"));

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(overview.getTimeAndCountPerDates()).hasSize(1);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableDate()).isEqualTo(_2023_02_08);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos()).hasSize(2);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(0).getTime()).isEqualTo(_12_00);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(0).getCount()).isEqualTo(1);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(1).getTime()).isEqualTo(_13_00);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(1).getCount()).isEqualTo(1);
        });
    }

    @Test
    void 여러_날짜가_있으면_각_날짜별로_TimeTableOverview를_반환한다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호"));
        var timeInfo2 = createTimeInfo(_13_00, List.of("이수진"));
        var dateInfo1 = createDateInfo(_2023_02_08, List.of(timeInfo1));
        var dateInfo2 = createDateInfo(_2023_02_09, List.of(timeInfo2));
        timeTable.replaceDateInfos(List.of(dateInfo1, dateInfo2));

        // when
        var overview = TimeTableOverview.from(timeTable, null);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(overview.getTimeAndCountPerDates()).hasSize(2);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableDate()).isEqualTo(_2023_02_08);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos()).hasSize(1);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(0).getTime()).isEqualTo(_12_00);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(0).getCount()).isEqualTo(1);

            softly.assertThat(overview.getTimeAndCountPerDates().get(1).getAvailableDate()).isEqualTo(_2023_02_09);
            softly.assertThat(overview.getTimeAndCountPerDates().get(1).getAvailableTimeInfos()).hasSize(1);
            softly.assertThat(overview.getTimeAndCountPerDates().get(1).getAvailableTimeInfos().get(0).getTime()).isEqualTo(_13_00);
            softly.assertThat(overview.getTimeAndCountPerDates().get(1).getAvailableTimeInfos().get(0).getCount()).isEqualTo(1);
        });
    }

    @Test
    void 해당_participantNames가_포함된_TimeInfo가_없으면_해당_날짜는_제외된다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호"));
        var timeInfo2 = createTimeInfo(_13_00, List.of("이수진"));
        var dateInfo1 = createDateInfo(_2023_02_08, List.of(timeInfo1));
        var dateInfo2 = createDateInfo(_2023_02_09, List.of(timeInfo2));
        timeTable.replaceDateInfos(List.of(dateInfo1, dateInfo2));

        // when
        var overview = TimeTableOverview.from(timeTable, List.of("이채민"));

        // then
        var timeAndCountPerDates = overview.getTimeAndCountPerDates();
        assertThat(timeAndCountPerDates).isEmpty();
    }

    @Test
    void TimeInfo가_없는_날짜는_제외된다() {
        // given
        var timeTable = new TimeTable(ROOM_UUID);
        var timeInfo1 = createTimeInfo(_12_00, List.of("김동호"));
        var dateInfo1 = createDateInfo(_2023_02_08, List.of(timeInfo1));
        var dateInfo2 = createDateInfo(_2023_02_09, List.of()); // 빈 TimeInfo
        timeTable.replaceDateInfos(List.of(dateInfo1, dateInfo2));

        // when
        var overview = TimeTableOverview.from(timeTable, null);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(overview.getTimeAndCountPerDates()).hasSize(1);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableDate()).isEqualTo(_2023_02_08);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos()).hasSize(1);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(0).getTime()).isEqualTo(_12_00);
            softly.assertThat(overview.getTimeAndCountPerDates().get(0).getAvailableTimeInfos().get(0).getCount()).isEqualTo(1);
        });
    }

    private TimeInfo createTimeInfo(LocalTime time, List<String> participantNames) {
        //language=JSON
        var literal = """
                {
                    "id": 1,
                    "time": "%s",
                    "timeInfoParticipantNames": []
                }
                """.formatted(time);

        var timeInfo = JsonUtils.readValue(literal, TimeInfo.class);
        for (String participantName : participantNames) {
            timeInfo.addParticipantName(participantName);
        }
        return timeInfo;
    }

    private DateInfo createDateInfo(LocalDate date, List<TimeInfo> timeInfos) {
        return new DateInfo(new TimeTable(ROOM_UUID), date, timeInfos);
    }
}
