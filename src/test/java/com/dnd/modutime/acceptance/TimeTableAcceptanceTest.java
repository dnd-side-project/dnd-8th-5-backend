package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;

import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class TimeTableAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 참여자가_가능한_시간을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        로그인_참여자1_1234(roomCreationResponse.getUuid());

        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));
        ExtractableResponse<Response> response = put("/api/room/" + roomCreationResponse.getUuid() + "/available-time", timeReplaceRequest);
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
