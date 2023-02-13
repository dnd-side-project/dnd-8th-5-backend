package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.dto.RoomRequest;
import com.dnd.modutime.dto.RoomResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class RoomAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 방을_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        ExtractableResponse<Response> response = post("/api/room", roomRequest);
        RoomResponse roomResponse = response.body().as(RoomResponse.class);
        assertThat(roomResponse.getUuid()).isNotNull();
    }
}
