package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequestNoTime;
import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._11_30;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._12_30;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._13_30;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static com.dnd.modutime.fixture.TimeFixture.getAvailableDateTimeRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.dto.response.AvailableTimeInfo;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import com.dnd.modutime.dto.response.TimeAndCountPerDate;
import com.dnd.modutime.dto.response.TimeTableResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TimeTableAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 방에_등록된_날짜와_시간당_참여자의수를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/available-time/group");
        TimeTableResponse timeTableResponse = response.body().as(TimeTableResponse.class);
        List<TimeAndCountPerDate> timeAndCountPerDates = timeTableResponse.getTimeAndCountPerDates();
        TimeAndCountPerDate _02_08 = timeAndCountPerDates.get(0);
        TimeAndCountPerDate _02_09 = timeAndCountPerDates.get(1);
        TimeAndCountPerDate _02_10 = timeAndCountPerDates.get(2);
        assertAll(
                () -> assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08),
                () -> assertThat(_02_08.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(_11_00, 3),
                        new AvailableTimeInfo(_11_30, 3),
                        new AvailableTimeInfo(_12_00, 1),
                        new AvailableTimeInfo(_12_30, 1),
                        new AvailableTimeInfo(_13_00, 2),
                        new AvailableTimeInfo(_13_30, 0)
                ),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(_11_00, 1),
                        new AvailableTimeInfo(_11_30, 1),
                        new AvailableTimeInfo(_12_00, 1),
                        new AvailableTimeInfo(_12_30, 0),
                        new AvailableTimeInfo(_13_00, 3),
                        new AvailableTimeInfo(_13_30, 1)
                ),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(_11_00, 1),
                        new AvailableTimeInfo(_11_30, 3),
                        new AvailableTimeInfo(_12_00, 1),
                        new AvailableTimeInfo(_12_30, 2),
                        new AvailableTimeInfo(_13_00, 1),
                        new AvailableTimeInfo(_13_30, 2)
                )
        );
    }

    @Test
    void 방에_등록된_날짜당_참여자의수를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        두명의_날짜를_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/available-time/group");
        TimeTableResponse timeTableResponse = response.body().as(TimeTableResponse.class);
        List<TimeAndCountPerDate> timeAndCountPerDates = timeTableResponse.getTimeAndCountPerDates();
        TimeAndCountPerDate _02_08 = timeAndCountPerDates.get(0);
        TimeAndCountPerDate _02_09 = timeAndCountPerDates.get(1);
        TimeAndCountPerDate _02_10 = timeAndCountPerDates.get(2);
        assertAll(
                () -> assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08),
                () -> assertThat(_02_08.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(null, 1)
                ),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(null, 0)
                ),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(null, 2)
                )
        );
    }

    private void 두명의_날짜를_등록한다(final String roomUuid) {
        로그인후_시간을_등록한다(roomUuid,
                "김동호",
                List.of(getAvailableDateTimeRequest(_2023_02_08, null),
                        getAvailableDateTimeRequest(_2023_02_10, null)
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이수진",
                List.of(getAvailableDateTimeRequest(_2023_02_10, null))
        );
    }

    private void 세명의_날짜와_시간을_등록한다(String roomUuid) {
        로그인후_시간을_등록한다(roomUuid,
                "김동호",
                List.of(getAvailableDateTimeRequest(_2023_02_08, List.of(_11_00, _11_30, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_09, List.of(_11_00, _11_30, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_10, List.of(_11_00, _11_30, _13_00))
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이수진",
                List.of(getAvailableDateTimeRequest(_2023_02_08, List.of(_11_00, _11_30, _12_00, _12_30, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_09, List.of(_13_00, _13_30)),
                        getAvailableDateTimeRequest(_2023_02_10, List.of(_11_30, _12_30, _13_30))
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이세희",
                List.of(getAvailableDateTimeRequest(_2023_02_08, List.of(_11_00, _11_30)),
                        getAvailableDateTimeRequest(_2023_02_09, List.of(_12_00, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_10, List.of(_11_30, _12_00, _12_30, _13_30))
                )
        );
    }
}
