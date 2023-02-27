package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture.getAvailableDateTimeRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.dto.request.EmailCreationRequest;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ParticipantAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 이메일을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String roomUuid = roomCreationResponse.getUuid();
        로그인_참여자_1234(roomUuid, "참여자1");
        시간을_등록한다(roomUuid, "참여자1", getAvailableDateTimeRequest(
                _2023_02_09, List.of(_11_00, _12_00)
        ));

        ExtractableResponse<Response> response = post("/api/room/" + roomUuid + "/email", new EmailCreationRequest(
                "참여자1", "dongho1088@gmail.com"
        ));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
