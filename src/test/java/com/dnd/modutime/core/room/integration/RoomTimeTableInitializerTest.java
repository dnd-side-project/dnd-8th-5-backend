package com.dnd.modutime.core.room.integration;

import static com.dnd.modutime.fixture.RoomFixture.*;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.modutime.core.room.application.RoomTimeTableInitializer;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;

@Transactional
@SpringBootTest
public class RoomTimeTableInitializerTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTimeTableInitializer roomTimeTableInitializer;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Test
    void 방의_날짜와_시작_끝시간에_해당하는_인원수를_0으로_초기화한다() {
        // given
        Room room = getRoom(_11_00, _13_00, List.of(_2023_02_09, _2023_02_10), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<Integer> counts = timeTable.getDateInfos().stream()
                .flatMap(it -> it.getTimeInfos().stream())
                .map(TimeInfo::getParticipantsSize)
                .collect(Collectors.toList());

        // then
        assertThat(counts).hasSize(8)
                .allMatch(it -> it.equals(0));
    }

    @Test
    void 방의_날짜에_해당하는_인원수를_0으로_초기화한다() {
        // given
        Room room = getRoom(null, null, List.of(_2023_02_09, _2023_02_10), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<Integer> counts = timeTable.getDateInfos().stream()
                .flatMap(it -> it.getTimeInfos().stream())
                .map(TimeInfo::getParticipantsSize)
                .collect(Collectors.toList());

        // then
        assertThat(counts).hasSize(2)
                .allMatch(it -> it.equals(0));
    }

    @Test
    void 시작시간이_끝시간보다_작을때_해당_정보에_맞게_time_table을_세팅한다() {
        // given
        Room room = getRoom(_11_00, _13_00, List.of(_2023_02_09), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<LocalTime> times = timeTable.getDateInfos().get(0).getTimeInfos().stream()
            .map(TimeInfo::getTime)
            .collect(Collectors.toList());

        // then
        assertThat(times).hasSize(4)
            .containsExactly(_11_00, _11_30, _12_00, _12_30);
    }

    @Test
    void 시작시간이_끝시간보다_클때_해당_정보에_맞게_time_table을_세팅한다() {
        // given
        Room room = getRoom(_22_00, _02_00, List.of(_2023_02_09), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<LocalTime> times = timeTable.getDateInfos().get(0).getTimeInfos().stream()
            .map(TimeInfo::getTime)
            .collect(Collectors.toList());

        // then
        assertThat(times).hasSize(8)
                .containsExactly(_00_00, _00_30, _01_00, _01_30, _22_00, _22_30, _23_00, _23_30);
    }

    @Test
    void 끝시간이_자정일때에_해당_정보에_맞게_time_table을_세팅한다() {
        // given
        Room room = getRoom(_22_00, _00_00, List.of(_2023_02_09), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<LocalTime> times = timeTable.getDateInfos().get(0).getTimeInfos().stream()
            .map(TimeInfo::getTime)
            .collect(Collectors.toList());

        // then
        assertThat(times).hasSize(4)
            .containsExactly(_22_00, _22_30, _23_00, _23_30);
    }
}
