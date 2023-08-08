package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.*;
import static com.dnd.modutime.fixture.TimeFixture.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

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

    protected RoomCreationResponse 시작시간이_끝시간보다_큰_방_생성() {
        RoomRequest roomRequest = getRoomRequestWithStartTimeIsAfterEndTime();
        ExtractableResponse<Response> response = post("/api/room", roomRequest);
        return response.body().as(RoomCreationResponse.class);
    }

    protected ExtractableResponse<Response> 로그인_참여자_1234(String roomUuid, String participantName) {
        LoginRequest loginRequest = new LoginRequest(participantName, "1234");
        return post("/api/room/" + roomUuid + "/login", loginRequest);
    }

    protected ExtractableResponse<Response> 시간을_등록한다(String roomUuid, String participantName, Boolean hasTime, LocalDateTime dateTime) {
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest(participantName, hasTime, List.of(dateTime));
        return put("/api/room/" + roomUuid + "/available-time", timeReplaceRequest);
    }

    protected ExtractableResponse<Response> 시간을_등록한다(String roomUuid, String participantName, Boolean hasTime, List<LocalDateTime> dateTimes) {
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest(participantName, hasTime, dateTimes);
        return put("/api/room/" + roomUuid + "/available-time", timeReplaceRequest);
    }

    protected void 로그인후_시간을_등록한다(String roomUuid, String participantName, Boolean hasTime, List<LocalDateTime> requests) {
        로그인_참여자_1234(roomUuid, participantName);
        시간을_등록한다(roomUuid, participantName, hasTime, requests);
    }

    protected EmailResponse 이메일을_조회한다(String roomUuid, String participantName) {
        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/email?name=" + participantName);
        return response.body().as(EmailResponse.class);
    }

    protected void 두명의_날짜를_등록한다(String roomUuid) {
        로그인후_시간을_등록한다(roomUuid,
                "김동호",
                false,
                List.of(LocalDateTime.of(_2023_02_08, _00_00),
                        LocalDateTime.of(_2023_02_10, _00_00)
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이수진",
                false,
                List.of(LocalDateTime.of(_2023_02_10, _00_00))
        );
    }

    protected void 세명의_날짜와_시간을_등록한다(String roomUuid) {
        로그인후_시간을_등록한다(roomUuid,
                "김동호",
                true,
                List.of(LocalDateTime.of(_2023_02_08, _11_00),
                        LocalDateTime.of(_2023_02_08, _11_30),
                        LocalDateTime.of(_2023_02_08, _13_00),

                        LocalDateTime.of(_2023_02_09, _11_00),
                        LocalDateTime.of(_2023_02_09, _11_30),
                        LocalDateTime.of(_2023_02_09, _13_00),

                        LocalDateTime.of(_2023_02_10, _11_00),
                        LocalDateTime.of(_2023_02_10, _11_30),
                        LocalDateTime.of(_2023_02_10, _13_00)
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이수진",
                true,
                List.of(LocalDateTime.of(_2023_02_08, _11_00),
                        LocalDateTime.of(_2023_02_08, _11_30),
                        LocalDateTime.of(_2023_02_08, _12_00),
                        LocalDateTime.of(_2023_02_08, _12_30),
                        LocalDateTime.of(_2023_02_08, _13_00),

                        LocalDateTime.of(_2023_02_09, _13_00),
                        LocalDateTime.of(_2023_02_09, _13_30),

                        LocalDateTime.of(_2023_02_10, _11_30),
                        LocalDateTime.of(_2023_02_10, _12_30),
                        LocalDateTime.of(_2023_02_10, _13_30)
                )
        );

        로그인후_시간을_등록한다(roomUuid,
                "이세희",
                true,
                List.of(LocalDateTime.of(_2023_02_08, _11_00),
                        LocalDateTime.of(_2023_02_08, _11_30),

                        LocalDateTime.of(_2023_02_09, _12_00),
                        LocalDateTime.of(_2023_02_09, _13_00),

                        LocalDateTime.of(_2023_02_10, _11_30),
                        LocalDateTime.of(_2023_02_10, _12_00),
                        LocalDateTime.of(_2023_02_10, _12_30),
                        LocalDateTime.of(_2023_02_10, _13_30)
                )
        );
    }
}
