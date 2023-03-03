package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequestNoTime;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class TimeBlockAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 참여자가_가능한_시간을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        ExtractableResponse<Response> response = 시간을_등록한다(roomCreationResponse.getUuid(), participantName, true,
                List.of(LocalDateTime.of(_2023_02_10, _12_00)));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 날짜만_등록된_방에_참여자가_가능한_시간을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime());
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        ExtractableResponse<Response> response = 시간을_등록한다(roomCreationResponse.getUuid(), participantName, false,
                List.of(LocalDateTime.of(_2023_02_10, LocalTime.of(0, 0))));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 참여자가_등록한_날짜와_시간을_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        시간을_등록한다(roomCreationResponse.getUuid(), participantName, true, List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));

        ExtractableResponse<Response> response = get("/api/room/" + roomCreationResponse.getUuid() + "/available-time?name=" + participantName);
        TimeBlockResponse timeBlockResponse = response.body().as(TimeBlockResponse.class);

        assertAll(
                () -> assertThat(timeBlockResponse.getName()).isEqualTo(participantName),
                () -> assertThat(timeBlockResponse.getAvailableDateTimes()).hasSize(2)
                        .contains(LocalDateTime.of(2023, 2, 10, 12, 0),
                                LocalDateTime.of(2023, 2, 10, 13, 0))
        );
    }

    @Test
    void 참여자가_등록한_날짜를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime());
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        시간을_등록한다(roomCreationResponse.getUuid(), participantName, false, LocalDateTime.of(_2023_02_10, LocalTime.of(0,0)));

        ExtractableResponse<Response> response = get("/api/room/" + roomCreationResponse.getUuid() + "/available-time?name=" + participantName);
        TimeBlockResponse timeBlockResponse = response.body().as(TimeBlockResponse.class);

        assertAll(
                () -> assertThat(timeBlockResponse.getName()).isEqualTo(participantName),
                () -> assertThat(timeBlockResponse.getAvailableDateTimes()).hasSize(1)
                        .contains(LocalDateTime.of(2023, 2, 10, 0, 0))
        );
    }
}
