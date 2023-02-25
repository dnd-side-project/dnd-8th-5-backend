package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomFixture.getRoomRequest;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.LoginRequest;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Import(TimeConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AcceptanceSupporter {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected ExtractableResponse<Response> get(String uri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> post(String uri, Object body) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post(uri)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> put(String uri, Object body) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    protected RoomCreationResponse 방_생성(RoomRequest roomRequest) {
        ExtractableResponse<Response> response = post("/api/room", roomRequest);
        return response.body().as(RoomCreationResponse.class);
    }

    protected RoomCreationResponse 방_생성() {
        RoomRequest roomRequest = getRoomRequest();
        ExtractableResponse<Response> response = post("/api/room", roomRequest);
        return response.body().as(RoomCreationResponse.class);
    }

    protected ExtractableResponse<Response> 로그인_참여자_1234(String roomUuid, String participantName) {
        LoginRequest loginRequest = new LoginRequest(participantName, "1234");
        return post("/api/room/" + roomUuid + "/login", loginRequest);
    }

    protected ExtractableResponse<Response> 시간을_등록한다(String roomUuid, String participantName, AvailableDateTimeRequest request) {
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest(participantName, List.of(request));
        return put("/api/room/" + roomUuid + "/available-time", timeReplaceRequest);
    }

    protected ExtractableResponse<Response> 시간을_등록한다(String roomUuid, String participantName, List<AvailableDateTimeRequest> requests) {
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest(participantName, requests);
        return put("/api/room/" + roomUuid + "/available-time", timeReplaceRequest);
    }

    protected void 로그인후_시간을_등록한다(String roomUuid, String participantName, List<AvailableDateTimeRequest> requests) {
        로그인_참여자_1234(roomUuid, participantName);
        시간을_등록한다(roomUuid, participantName, requests);
    }
}
