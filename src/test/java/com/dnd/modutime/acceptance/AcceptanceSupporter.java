package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._11_30;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._12_30;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._13_30;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    protected RoomCreationResponse ???_??????(RoomRequest roomRequest) {
        ExtractableResponse<Response> response = post("/api/room", roomRequest);
        return response.body().as(RoomCreationResponse.class);
    }

    protected RoomCreationResponse ???_??????() {
        RoomRequest roomRequest = getRoomRequest();
        ExtractableResponse<Response> response = post("/api/room", roomRequest);
        return response.body().as(RoomCreationResponse.class);
    }

    protected ExtractableResponse<Response> ?????????_?????????_1234(String roomUuid, String participantName) {
        LoginRequest loginRequest = new LoginRequest(participantName, "1234");
        return post("/api/room/" + roomUuid + "/login", loginRequest);
    }

    protected ExtractableResponse<Response> ?????????_????????????(String roomUuid, String participantName, Boolean hasTime, LocalDateTime dateTime) {
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest(participantName, hasTime, List.of(dateTime));
        return put("/api/room/" + roomUuid + "/available-time", timeReplaceRequest);
    }

    protected ExtractableResponse<Response> ?????????_????????????(String roomUuid, String participantName, Boolean hasTime, List<LocalDateTime> dateTimes) {
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest(participantName, hasTime, dateTimes);
        return put("/api/room/" + roomUuid + "/available-time", timeReplaceRequest);
    }

    protected void ????????????_?????????_????????????(String roomUuid, String participantName, Boolean hasTime, List<LocalDateTime> requests) {
        ?????????_?????????_1234(roomUuid, participantName);
        ?????????_????????????(roomUuid, participantName, hasTime, requests);
    }

    protected EmailResponse ????????????_????????????(String roomUuid, String participantName) {
        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/email?name=" + participantName);
        return response.body().as(EmailResponse.class);
    }

    protected void ?????????_?????????_????????????(String roomUuid) {
        ????????????_?????????_????????????(roomUuid,
                "?????????",
                false,
                List.of(LocalDateTime.of(_2023_02_08, LocalTime.of(0, 0)),
                        LocalDateTime.of(_2023_02_10, LocalTime.of(0, 0))
                )
        );
        ????????????_?????????_????????????(roomUuid,
                "?????????",
                false,
                List.of(LocalDateTime.of(_2023_02_10, LocalTime.of(0, 0)))
        );
    }

    protected void ?????????_?????????_?????????_????????????(String roomUuid) {
        ????????????_?????????_????????????(roomUuid,
                "?????????",
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
        ????????????_?????????_????????????(roomUuid,
                "?????????",
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

        ????????????_?????????_????????????(roomUuid,
                "?????????",
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
