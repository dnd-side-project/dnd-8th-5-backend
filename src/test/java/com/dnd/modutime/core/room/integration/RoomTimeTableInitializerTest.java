package com.dnd.modutime.core.room.integration;

import static com.dnd.modutime.fixture.RoomFixture.*;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
        List<TimeInfo> timeInfos = timeTable.getDateInfos().get(0).getTimeInfos();

        // then
        assertAll(
            () -> assertThat(timeInfos.get(0).getTime().equals(_11_00)),
            () -> assertThat(timeInfos.get(1).getTime().equals(_11_30)),
            () -> assertThat(timeInfos.get(2).getTime().equals(_12_00)),
            () -> assertThat(timeInfos.get(3).getTime().equals(_12_30))
        );
    }

    @Test
    void 시작시간이_끝시간보다_클때_해당_정보에_맞게_time_table을_세팅한다() {
        // given
        Room room = getRoom(_22_00, _02_00, List.of(_2023_02_09), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<TimeInfo> timeInfos = timeTable.getDateInfos().get(0).getTimeInfos();

        // then
        assertAll(
            () -> assertThat(timeInfos.get(4).getTime().equals(_22_00)),
            () -> assertThat(timeInfos.get(5).getTime().equals(_22_30)),
            () -> assertThat(timeInfos.get(6).getTime().equals(_23_00)),
            () -> assertThat(timeInfos.get(7).getTime().equals(_23_30)),
            () -> assertThat(timeInfos.get(0).getTime().equals(_00_00)),
            () -> assertThat(timeInfos.get(1).getTime().equals(_00_30)),
            () -> assertThat(timeInfos.get(2).getTime().equals(_01_00)),
            () -> assertThat(timeInfos.get(3).getTime().equals(_01_30))
        );
    }

    @Test
    void 끝시간이_자정일때에_해당_정보에_맞게_time_table을_세팅한다() {
        // given
        Room room = getRoom(_22_00, _00_00, List.of(_2023_02_09), 2);
        roomRepository.save(room);
        TimeTable timeTable = timeTableRepository.save(new TimeTable(room.getUuid()));
        roomTimeTableInitializer.initialize(room.getUuid(), timeTable);

        // when
        List<TimeInfo> timeInfos = timeTable.getDateInfos().get(0).getTimeInfos();

        // then
        assertAll(
            () -> assertThat(timeInfos.get(0).getTime().equals(_22_00)),
            () -> assertThat(timeInfos.get(1).getTime().equals(_22_30)),
            () -> assertThat(timeInfos.get(2).getTime().equals(_23_00)),
            () -> assertThat(timeInfos.get(3).getTime().equals(_23_30))
        );
    }
}
