package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dnd.modutime.acceptance.request.RoomRequestWithNoNull;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.V2RoomInfoResponse;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RoomAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 시작시간이_끝시간보다_작은_방을_생성한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    @Test
    void 시작시간이_끝시간보다_큰_방을_생성한다() {
        RoomCreationResponse roomCreationResponse = 시작시간이_끝시간보다_큰_방_생성();
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    @Test
    void 방_정보를_응답한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        ExtractableResponse<Response> response = get("/api/v2/room/" + roomCreationResponse.getUuid());
        V2RoomInfoResponse roomInfoResponse = response.body().as(V2RoomInfoResponse.class);
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

        ExtractableResponse<Response> response = get("/api/v2/room/" + roomCreationResponse.getUuid());
        V2RoomInfoResponse roomInfoResponse = response.body().as(V2RoomInfoResponse.class);
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

    @Test
    void 빈_본문으로_방_생성_요청시_400을_응답한다() {
        ExtractableResponse<Response> response = postRaw("/api/room", "{}");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isNotBlank()
        );
    }

    @Test
    void 깨진_JSON으로_방_생성_요청시_400을_응답한다() {
        ExtractableResponse<Response> response = postRaw("/api/room", "{");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("잘못된 요청입니다.")
        );
    }

    @Test
    void 제목이_없으면_400을_응답한다() {
        Map<String, Object> body = Map.of(
                "dates", List.of("2023-02-10"),
                "headCount", 5
        );

        ExtractableResponse<Response> response = post("/api/room", body);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("방의 제목은 빈문자일 수 없습니다.")
        );
    }

    @Test
    void 날짜가_비어있으면_400을_응답한다() {
        Map<String, Object> body = Map.of(
                "title", "이멤버리멤버",
                "dates", List.of(),
                "headCount", 5
        );

        ExtractableResponse<Response> response = post("/api/room", body);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("날짜는 최소 1개이상 존재해야 합니다.")
        );
    }

    private ExtractableResponse<Response> postRaw(String uri, String body) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(uri)
                .then().log().all()
                .extract();
    }
}
