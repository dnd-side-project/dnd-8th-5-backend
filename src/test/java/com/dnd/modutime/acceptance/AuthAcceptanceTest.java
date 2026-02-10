package com.dnd.modutime.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.auth.application.response.GuestLoginResponse;
import com.dnd.modutime.core.auth.application.response.LoginPageResponse;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class AuthAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 방에_존재하지_않는_이름과_패스워드로_로그인요청을_하면_200_상태코드를_반환한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        ExtractableResponse<Response> response = 로그인_참여자_1234(roomCreationResponse.getUuid(), "참여자1");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 방에_이미존재하는이름과_올바른_패스워드로_로그인요청을_하면_200_상태코드를_반환한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        로그인_참여자_1234(roomCreationResponse.getUuid(), "참여자1");

        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        ExtractableResponse<Response> response = post("/api/room/" + roomCreationResponse.getUuid() + "/login", loginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 방에_이미존재하는이름과_올바르지_않은_패스워드로_로그인요청을_하면_401_상태코드를_반환한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        로그인_참여자_1234(roomCreationResponse.getUuid(), "참여자1");

        LoginRequest invalidLoginRequest = new LoginRequest("참여자1", "9999");
        ExtractableResponse<Response> response = post("/api/room/" + roomCreationResponse.getUuid() + "/login", invalidLoginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 로그인페이지_입장시_200_상태코드와_로그인페이지에_필요한_정보를_반환한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        ExtractableResponse<Response> response = get("/api/room/" + roomCreationResponse.getUuid() + "/login");
        LoginPageResponse loginPageResponse = response.body().as(LoginPageResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(loginPageResponse.getRoomTitle()).isEqualTo("이멤버리멤버")
        );
    }

    @Test
    void Guest_V1_로그인시_JWT_토큰이_반환된다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        ExtractableResponse<Response> response = post("/guest/api/v1/room/" + roomCreationResponse.getUuid() + "/login", loginRequest);
        GuestLoginResponse guestLoginResponse = response.body().as(GuestLoginResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(guestLoginResponse.getAccessToken()).isNotBlank(),
                () -> assertThat(guestLoginResponse.getAccessTokenExpireTime()).isNotNull()
        );
    }

    @Test
    void Guest_V1_잘못된_비밀번호로_로그인시_401을_반환한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        로그인_참여자_1234(roomCreationResponse.getUuid(), "참여자1");

        LoginRequest invalidLoginRequest = new LoginRequest("참여자1", "9999");
        ExtractableResponse<Response> response = post("/guest/api/v1/room/" + roomCreationResponse.getUuid() + "/login", invalidLoginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
