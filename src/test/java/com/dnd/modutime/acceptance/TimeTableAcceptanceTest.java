package com.dnd.modutime.acceptance;

import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.timetable.application.response.AvailableTimeInfo;
import com.dnd.modutime.core.timetable.application.response.TimeAndCountPerDate;
import com.dnd.modutime.core.timetable.application.response.TimeTableResponse;
import com.dnd.modutime.core.timetable.domain.view.TimeTableOverview;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequestNoTime;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
                () -> assertThat(_02_08.getAvailableTimeInfos()).extracting("time")
                        .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).extracting("time")
                        .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).extracting("time")
                        .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30)
        );
    }

    @Test
    void v2_방에_등록된_날짜와_시간당_참여자의수를_조회한다() {
        var roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        var roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        var response = get("/api/room/" + roomUuid + "/available-time/overview");
        var view = response.body().as(TimeTableOverview.class);
        var timeAndCountPerDates = view.getTimeAndCountPerDates();
        var _02_08 = timeAndCountPerDates.get(0);
        var _02_09 = timeAndCountPerDates.get(1);
        var _02_10 = timeAndCountPerDates.get(2);
        assertAll(
                () -> assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08),
                () -> assertThat(_02_08.getAvailableTimeInfos()).extracting("time")
                        .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).extracting("time")
                        .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).extracting("time")
                        .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30)
        );
    }

    @Test
    void v2_특정_참여자의_시간만_조회한다() {
        var roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        var roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        var response = get("/api/room/" + roomUuid + "/available-time/overview?participantNames=김동호");
        var view = response.body().as(TimeTableOverview.class);
        var timeAndCountPerDates = view.getTimeAndCountPerDates();

        assertAll(
                () -> assertThat(timeAndCountPerDates).hasSize(3),
                () -> {
                    var _02_08 = timeAndCountPerDates.get(0);
                    assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08);
                    assertThat(_02_08.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },
                () -> {
                    var _02_09 = timeAndCountPerDates.get(1);
                    assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09);
                    assertThat(_02_09.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },
                () -> {
                    var _02_10 = timeAndCountPerDates.get(2);
                    assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10);
                    assertThat(_02_10.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                }
        );
    }

    @Test
    void v2_여러_참여자의_시간만_조회한다() {
        var roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        var roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        var response = get("/api/room/" + roomUuid + "/available-time/overview?participantNames=김동호,이수진");
        var view = response.body().as(TimeTableOverview.class);
        var timeAndCountPerDates = view.getTimeAndCountPerDates();

        assertAll(
                () -> assertThat(timeAndCountPerDates).hasSize(3),
                () -> {
                    var _02_08 = timeAndCountPerDates.get(0);
                    assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08);
                    assertThat(_02_08.getAvailableTimeInfos()).hasSize(6);
                    assertThat(_02_08.getAvailableTimeInfos()).extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },
                () -> {
                    var _02_09 = timeAndCountPerDates.get(1);
                    assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09);
                    assertThat(_02_09.getAvailableTimeInfos()).hasSize(6);
                    assertThat(_02_09.getAvailableTimeInfos()).extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },
                () -> {
                    var _02_10 = timeAndCountPerDates.get(2);
                    assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10);
                    assertThat(_02_10.getAvailableTimeInfos()).hasSize(6);
                    assertThat(_02_10.getAvailableTimeInfos()).extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                }
        );
    }

    @Test
    void v2_빈_시간대도_응답에_포함한다() {
        // given - 4개 날짜로 방을 생성하되, 일부 날짜에만 시간을 등록
        var roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10, _2023_02_11)));
        var roomUuid = roomCreationResponse.getUuid();

        // 2023-02-08, 2023-02-09에만 시간을 등록 (2023-02-10, 2023-02-11은 빈 상태)
        시간을_등록한다(roomUuid, "김동호", true, List.of(
                LocalDateTime.of(_2023_02_08, _11_00),
                LocalDateTime.of(_2023_02_08, _12_00),
                LocalDateTime.of(_2023_02_09, _13_00)
        ));
        시간을_등록한다(roomUuid, "이수진", true, List.of(
                LocalDateTime.of(_2023_02_08, _11_00),
                LocalDateTime.of(_2023_02_09, _13_00)
        ));

        // when
        var response = get("/api/room/" + roomUuid + "/available-time/overview");
        var view = response.body().as(TimeTableOverview.class);
        var timeAndCountPerDates = view.getTimeAndCountPerDates();

        // then
        assertAll(
                // 4개 날짜 모두 응답에 포함되어야 함
                () -> assertThat(timeAndCountPerDates).hasSize(4),

                // 2023-02-08: 시간대가 있는 날짜
                () -> {
                    var _02_08 = timeAndCountPerDates.get(0);
                    assertThat(_02_08.getAvailableDate()).isEqualTo(_2023_02_08);
                    assertThat(_02_08.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactlyInAnyOrder(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },

                // 2023-02-09: 시간대가 있는 날짜
                () -> {
                    var _02_09 = timeAndCountPerDates.get(1);
                    assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09);
                    assertThat(_02_09.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactly(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },

                // 2023-02-10: 빈 시간대
                () -> {
                    var _02_10 = timeAndCountPerDates.get(2);
                    assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10);
                    assertThat(_02_10.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactly(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                },

                // 2023-02-11: 빈 시간대
                () -> {
                    var _02_11 = timeAndCountPerDates.get(3);
                    assertThat(_02_11.getAvailableDate()).isEqualTo(_2023_02_11);
                    assertThat(_02_11.getAvailableTimeInfos())
                            .hasSize(6)
                            .extracting("time")
                            .containsExactly(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30);
                }
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
                () -> assertThat(_02_08.getAvailableTimeInfos()).extracting("time")
                        .containsExactly(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).extracting("time")
                        .containsExactly(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).extracting("time")
                        .containsExactly(_11_00, _11_30, _12_00, _12_30, _13_00, _13_30)
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
                () -> assertThat(_02_08.getAvailableTimeInfos()).extracting("count")
                        .containsExactly(0),
                () -> assertThat(_02_09.getAvailableDate()).isEqualTo(_2023_02_09),
                () -> assertThat(_02_09.getAvailableTimeInfos()).extracting("count")
                        .containsExactly(1),
                () -> assertThat(_02_10.getAvailableDate()).isEqualTo(_2023_02_10),
                () -> assertThat(_02_10.getAvailableTimeInfos()).extracting("count")
                        .containsExactly(1)
        );
    }
}
