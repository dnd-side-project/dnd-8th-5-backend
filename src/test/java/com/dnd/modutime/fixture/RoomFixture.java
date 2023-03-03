package com.dnd.modutime.fixture;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10_00_00;

import com.dnd.modutime.util.FakeTimeProvider;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
import com.dnd.modutime.util.TimeProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class RoomFixture {

    public static Room getRoom() {
        List<LocalDate> dates = List.of(_2023_02_10);
        return getRoom(_12_00, _13_00, dates, 10);
    }

    public static Room getRoomByStartEndTime(LocalTime startTime, LocalTime endTime) {
        return getRoom(startTime, endTime, List.of(_2023_02_10), 1);
    }

    public static Room getRoomByTitle(String title) {
        List<LocalDate> dates = List.of(_2023_02_10);
        return getRoom(title, _12_00, _13_00, dates, 1, _2023_02_10_00_00, new FakeTimeProvider());
    }

    public static Room getRoom(List<LocalDate> dates) {
        return getRoom(_12_00, _13_00, dates, 1);
    }

    public static Room getRoomByHeadCount(Integer headCount) {
        return getRoom(_12_00, _13_00, List.of(_2023_02_10), headCount);
    }

    public static Room getRoom(LocalTime startTime,
                         LocalTime endTime,
                         List<LocalDate> dates,
                         Integer headCount) {
        return getRoom(startTime, endTime, dates, headCount, _2023_02_10_00_00);
    }

    public static Room getRoom(LocalDateTime deadLine, TimeProvider timeProvider) {
        List<LocalDate> dates = List.of(_2023_02_10);
        return getRoom(_12_00, _13_00, dates, 10, deadLine, timeProvider);
    }

    public static Room getRoom(LocalTime startTime,
                         LocalTime endTime,
                         List<LocalDate> dates,
                         Integer headCount,
                         LocalDateTime deadLine) {
        return getRoom(startTime, endTime, dates, headCount, deadLine, new FakeTimeProvider());
    }

    public static Room getRoom(LocalTime startTime,
                               LocalTime endTime,
                               List<LocalDate> dates,
                               Integer headCount,
                               LocalDateTime deadLine,
                               TimeProvider timeProvider) {
        return getRoom("이멤버 리멤버", startTime, endTime, dates, headCount, deadLine, timeProvider);
    }

    public static Room getRoomByRoomDates(List<RoomDate> roomDates) {
        return new Room("title", _12_00, _13_00, roomDates, 1, _2023_02_10_00_00, new FakeTimeProvider());
    }

    public static Room getRoom(String title,
                               LocalTime startTime,
                               LocalTime endTime,
                               List<LocalDate> dates,
                               Integer headCount,
                               LocalDateTime deadLine,
                               TimeProvider timeProvider) {
        return new Room(title, startTime, endTime,
                dates.stream()
                .map(RoomDate::new)
                .collect(Collectors.toList()),
                headCount, deadLine, timeProvider);
    }
}
