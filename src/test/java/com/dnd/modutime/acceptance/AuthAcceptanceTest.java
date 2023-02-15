package com.dnd.modutime.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.dto.request.LoginRequest;
import com.dnd.modutime.dto.response.RoomResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class AuthAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 방에_존재하지_않는_이름과_패스워드로_로그인요청을_하면_200_상태코드를_반환한다() {
        RoomResponse roomResponse = 방_생성();
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        ExtractableResponse<Response> response = post("/api/room/" + roomResponse.getUuid() + "/login", loginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 방에_이미존재하는이름과_올바른_패스워드로_로그인요청을_하면_200_상태코드를_반환한다() {
        RoomResponse roomResponse = 방_생성();
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        post("/api/room/" + roomResponse.getUuid() + "/login", loginRequest);

        ExtractableResponse<Response> response = post("/api/room/" + roomResponse.getUuid() + "/login", loginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 방에_이미존재하는이름과_올바르지_않은_패스워드로_로그인요청을_하면_401_상태코드를_반환한다() {
        RoomResponse roomResponse = 방_생성();
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        post("/api/room/" + roomResponse.getUuid() + "/login", loginRequest);

        LoginRequest invalidLoginRequest = new LoginRequest("참여자1", "9999");
        ExtractableResponse<Response> response = post("/api/room/" + roomResponse.getUuid() + "/login", invalidLoginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
