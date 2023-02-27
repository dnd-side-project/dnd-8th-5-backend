package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequestNoTime;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static com.dnd.modutime.fixture.TimeFixture.getAvailableDateTimeRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.dto.response.AvailableDateTimeResponse;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import com.dnd.modutime.dto.response.TimeBlockResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class TimeBlockAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 참여자가_가능한_시간을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        ExtractableResponse<Response> response = 시간을_등록한다(roomCreationResponse.getUuid(), participantName,
                getAvailableDateTimeRequest(_2023_02_10, List.of(_12_00)));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 날짜만_등록된_방에_참여자가_가능한_시간을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime());
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        ExtractableResponse<Response> response = 시간을_등록한다(roomCreationResponse.getUuid(), participantName,
                getAvailableDateTimeRequest(_2023_02_10, null));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 참여자가_등록한_시간을_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String participantName = "참여자1";
        로그인_참여자_1234(roomCreationResponse.getUuid(), participantName);
        시간을_등록한다(roomCreationResponse.getUuid(), participantName, getAvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00)));

        ExtractableResponse<Response> response = get("/api/room/" + roomCreationResponse.getUuid() + "/available-time?name=" + participantName);
        TimeBlockResponse timeBlockResponse = response.body().as(TimeBlockResponse.class);
        assertAll(
                () -> assertThat(timeBlockResponse.getName()).isEqualTo(participantName),
                () -> assertThat(timeBlockResponse.getAvailableDateTimes().stream()
                        .map(AvailableDateTimeResponse::getAvailableDate)
                        .collect(Collectors.toList())).hasSize(1)
                        .contains(_2023_02_10),
                () -> assertThat(timeBlockResponse.getAvailableDateTimes().stream()
                        .flatMap(it -> it.getAvailableTimes().stream())
                        .collect(Collectors.toList())).hasSize(2)
                        .contains(_12_00, _13_00)
        );
    }
}
