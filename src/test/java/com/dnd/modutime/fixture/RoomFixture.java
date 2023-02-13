package com.dnd.modutime.fixture;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;

import com.dnd.modutime.dto.RoomRequest;
import com.dnd.modutime.dto.TimerRequest;
import java.util.List;

public class RoomFixture {

    public static RoomRequest getRoomRequest(TimerRequest timerRequest) {
        return new RoomRequest("이멤버리멤버",
                10, List.of(_2023_02_10), _12_00, _13_00, timerRequest);
    }

    public static RoomRequest getRoomRequest() {
        TimerRequest timerRequest = new TimerRequest(2, 1, 30);
        return new RoomRequest("이멤버리멤버",
                10, List.of(_2023_02_10), _12_00, _13_00, timerRequest);
    }
}
