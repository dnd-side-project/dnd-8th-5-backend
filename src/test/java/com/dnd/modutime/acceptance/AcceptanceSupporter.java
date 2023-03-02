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
import static com.dnd.modutime.fixture.TimeFixture.getAvailableDateTimeRequest;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.LoginRequest;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.dto.response.EmailResponse;
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

    protected EmailResponse 이메일을_조회한다(String roomUuid, String participantName) {
        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/email?name=" + participantName);
        return response.body().as(EmailResponse.class);
    }

    protected void 두명의_날짜를_등록한다(String roomUuid) {
        로그인후_시간을_등록한다(roomUuid,
                "김동호",
                List.of(getAvailableDateTimeRequest(_2023_02_08, null),
                        getAvailableDateTimeRequest(_2023_02_10, null)
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이수진",
                List.of(getAvailableDateTimeRequest(_2023_02_10, null))
        );
    }

    protected void 세명의_날짜와_시간을_등록한다(String roomUuid) {
        로그인후_시간을_등록한다(roomUuid,
                "김동호",
                List.of(getAvailableDateTimeRequest(_2023_02_08, List.of(_11_00, _11_30, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_09, List.of(_11_00, _11_30, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_10, List.of(_11_00, _11_30, _13_00))
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이수진",
                List.of(getAvailableDateTimeRequest(_2023_02_08, List.of(_11_00, _11_30, _12_00, _12_30, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_09, List.of(_13_00, _13_30)),
                        getAvailableDateTimeRequest(_2023_02_10, List.of(_11_30, _12_30, _13_30))
                )
        );
        로그인후_시간을_등록한다(roomUuid,
                "이세희",
                List.of(getAvailableDateTimeRequest(_2023_02_08, List.of(_11_00, _11_30)),
                        getAvailableDateTimeRequest(_2023_02_09, List.of(_12_00, _13_00)),
                        getAvailableDateTimeRequest(_2023_02_10, List.of(_11_30, _12_00, _12_30, _13_30))
                )
        );
    }
}
