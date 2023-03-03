package com.dnd.modutime.fixture;

import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;

import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.room.application.request.TimerRequest;
import java.time.LocalDate;
import java.util.List;

public class RoomRequestFixture {

    public static String ROOM_UUID = "7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9";

    public static RoomRequest getRoomRequestNoTime() {
        return getRoomRequestNoTime(List.of(_2023_02_10));
    }

    public static RoomRequest getRoomRequestNoTime(List<LocalDate> dates) {
        TimerRequest timerRequest = new TimerRequest(2, 1, 30);
        return new RoomRequest("이멤버리멤버",
                10, dates, null, null, timerRequest);
    }

    public static RoomRequest getRoomRequest() {
        TimerRequest timerRequest = new TimerRequest(2, 1, 30);
        return getRoomRequest(timerRequest);
    }

    public static RoomRequest getRoomRequest(List<LocalDate> dates) {
        TimerRequest timerRequest = new TimerRequest(2, 1, 30);
        return getRoomRequest(dates, timerRequest);
    }

    public static RoomRequest getRoomRequest(TimerRequest timerRequest) {
        return getRoomRequest(List.of(_2023_02_10), timerRequest);
    }

    public static RoomRequest getRoomRequest(List<LocalDate> dates, TimerRequest timerRequest) {
        return new RoomRequest("이멤버리멤버",
                10, dates, _11_00, _14_00, timerRequest);
    }
}
