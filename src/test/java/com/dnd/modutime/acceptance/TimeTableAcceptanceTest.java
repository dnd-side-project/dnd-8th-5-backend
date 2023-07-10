package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequestNoTime;
import static com.dnd.modutime.fixture.TimeFixture._00_00;
import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._11_30;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._12_30;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._13_30;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.core.timetable.application.response.AvailableTimeInfo;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.timetable.application.response.TimeAndCountPerDate;
import com.dnd.modutime.core.timetable.application.response.TimeTableResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
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

    @Test
    void 시간이_있는_방의_참여자가_시간을_수정하면_TimeTable에도_반영되어야_한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);
        // 2023.02.10 13:00 의 시간만 제거
        시간을_등록한다(roomUuid, "김동호", true, List.of(LocalDateTime.of(_2023_02_08, _11_00),
                LocalDateTime.of(_2023_02_08, _11_30),
                LocalDateTime.of(_2023_02_08, _13_00),

                LocalDateTime.of(_2023_02_09, _11_00),
                LocalDateTime.of(_2023_02_09, _11_30),
                LocalDateTime.of(_2023_02_09, _13_00),

                LocalDateTime.of(_2023_02_10, _11_00),
                LocalDateTime.of(_2023_02_10, _11_30)
        ));

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
                        new AvailableTimeInfo(_13_00, 0),
                        new AvailableTimeInfo(_13_30, 2)
                )
        );
    }

    @Test
    void 시간이_없는_방의_참여자가_시간을_수정하면_TimeTable에도_반영되어야_한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        두명의_날짜를_등록한다(roomUuid);
        시간을_등록한다(roomUuid, "김동호", false,
                List.of(LocalDateTime.of(_2023_02_09, _00_00))
        );

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/available-time/group");
        TimeTableResponse timeTableResponse = response.body().as(TimeTableResponse.class);
        List<TimeAndCountPerDate> timeAndCountPerDates = timeTableResponse.getTimeAndCountPerDates();
        TimeAndCountPerDate _02_08 = timeAndCountPerDates.get(0);
        TimeAndCountPerDate _02_09 = timeAndCountPerDates.get(1);
        TimeAndCountPerDate _02_10 = timeAndCountPerDates.get(2);
        assertAll(
                () -> assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08),
                () -> assertThat(_02_08.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(null, 0)
                ),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(null, 1)
                ),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).contains(
                        new AvailableTimeInfo(null, 1)
                )
        );
    }
}
