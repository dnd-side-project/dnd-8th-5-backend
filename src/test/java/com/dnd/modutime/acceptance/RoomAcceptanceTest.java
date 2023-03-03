package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.acceptance.request.RoomRequestWithNoNull;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.RoomInfoResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RoomAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 방을_생성한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    @Test
    void 방_정보를_응답한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        ExtractableResponse<Response> response = get("/api/room/" + roomCreationResponse.getUuid());
        RoomInfoResponse roomInfoResponse = response.body().as(RoomInfoResponse.class);
        assertAll(
                () -> assertThat(roomInfoResponse.getTitle()).isEqualTo("이멤버리멤버"),
                () -> assertThat(roomInfoResponse.getDeadLine()).isNotNull(),
                () -> assertThat(roomInfoResponse.getHeadCount()).isEqualTo(10),
                () -> assertThat(roomInfoResponse.getDates())
                        .hasSize(1)
                        .contains(_2023_02_10),
                () -> assertThat(roomInfoResponse.getStartTime()).isEqualTo(_11_00),
                () -> assertThat(roomInfoResponse.getEndTime()).isEqualTo(_14_00)
        );
    }

    @Test
    void 방_정보를_응답한다_없는_데이터는_null로_응답한다() {
        RoomCreationResponse roomCreationResponse = getRoomCreationResponse();

        ExtractableResponse<Response> response = get("/api/room/" + roomCreationResponse.getUuid());
        RoomInfoResponse roomInfoResponse = response.body().as(RoomInfoResponse.class);
        assertAll(
                () -> assertThat(roomInfoResponse.getTitle()).isEqualTo("이멤버리멤버"),
                () -> assertThat(roomInfoResponse.getDeadLine()).isNull(),
                () -> assertThat(roomInfoResponse.getHeadCount()).isNull(),
                () -> assertThat(roomInfoResponse.getDates())
                        .hasSize(1)
                        .contains(_2023_02_10),
                () -> assertThat(roomInfoResponse.getStartTime()).isNull(),
                () -> assertThat(roomInfoResponse.getEndTime()).isNull()
        );
    }

    private RoomCreationResponse getRoomCreationResponse() {
        ExtractableResponse<Response> response = post("/api/room", new RoomRequestWithNoNull(
                "이멤버리멤버",
                List.of(_2023_02_10)));
        return response.body().as(RoomCreationResponse.class);
    }
}
