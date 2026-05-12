package com.dnd.modutime.acceptance;

import com.dnd.modutime.core.auth.oauth.controller.dto.KakaoNativeLoginResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2ReIssueTokenResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.ReissueTokenRequest;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 네이티브 카카오 로그인 흐름 acceptance.
 *
 * <p>{@code KakaoStubClient}가 활성화되어 외부 카카오 호출 없이 검증한다.
 * - "invalid" 토큰: 인증 거절 (401)
 * - "no-email" 토큰: 이메일 미동의 (403)
 * - 그 외: 토큰 hash 기반의 결정적 응답</p>
 */
public class KakaoNativeLoginAcceptanceTest extends AcceptanceSupporter {

    private static final String VALID_KAKAO_TOKEN = "valid-kakao-access-token-a";
    private static final String DIFFERENT_KAKAO_TOKEN = "valid-kakao-access-token-b";
    private static final String ROOM_UUID = "550e8400-e29b-41d4-a716-446655440000";

    @Autowired
    private ObjectMapper objectMapper;

    // AcceptanceSupporter 에서 @MockBean 으로 등록된 OAuth2TokenProvider 를 동일 빈으로 주입받는다.
    @Autowired
    private OAuth2TokenProvider oAuth2TokenProvider;

    @BeforeEach
    void stubTokenProvider() {
        // AcceptanceSupporter 가 OAuth2TokenProvider 를 @MockBean 으로 잡기 때문에
        // JWT 발급 동작을 명시적으로 stub 해야 한다.
        when(oAuth2TokenProvider.createOAuth2JwtTokenResponse(any(), any())).thenAnswer(inv -> {
            long now = System.currentTimeMillis();
            return new com.dnd.modutime.core.auth.oauth.dto.JwtTokenResponse(
                    "issued-access-token-" + now,
                    new java.util.Date(now + 60_000),
                    "issued-refresh-token-" + now,
                    new java.util.Date(now + 1_209_600_000L)
            );
        });
    }

    @Test
    @DisplayName("네이티브 로그인 - stub 토큰으로 자체 JWT + 사용자 정보를 받는다")
    void 네이티브_로그인_성공() throws Exception {
        ExtractableResponse<Response> response = 네이티브_로그인(VALID_KAKAO_TOKEN, ROOM_UUID);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        KakaoNativeLoginResponse body = response.body().as(KakaoNativeLoginResponse.class);
        assertAll(
                () -> assertThat(body.accessToken()).isNotBlank(),
                () -> assertThat(body.accessTokenExpirationTime()).isNotNull(),
                () -> assertThat(body.refreshToken()).isNotBlank(),
                () -> assertThat(body.refreshTokenExpirationTime()).isNotNull(),
                () -> assertThat(body.user().email()).isNotBlank(),
                () -> assertThat(body.user().name()).isNotBlank(),
                () -> assertThat(body.roomUuid()).isEqualTo(ROOM_UUID)
        );
    }

    @Test
    @DisplayName("네이티브 로그인 - roomUuid 없이도 동작한다")
    void 네이티브_로그인_roomUuid_없음() {
        ExtractableResponse<Response> response = 네이티브_로그인(VALID_KAKAO_TOKEN, null);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(KakaoNativeLoginResponse.class).roomUuid()).isNull();
    }

    @Test
    @DisplayName("네이티브 로그인 - 같은 토큰으로 두 번 호출하면 같은 사용자가 재로그인된다")
    void 네이티브_로그인_재로그인_동일_사용자() {
        var first = 네이티브_로그인(VALID_KAKAO_TOKEN, null).body().as(KakaoNativeLoginResponse.class);
        var second = 네이티브_로그인(VALID_KAKAO_TOKEN, null).body().as(KakaoNativeLoginResponse.class);

        assertThat(first.user().email()).isEqualTo(second.user().email());
    }

    @Test
    @DisplayName("네이티브 로그인 - 다른 토큰은 다른 사용자(이메일)로 식별된다")
    void 네이티브_로그인_다른_토큰_다른_사용자() {
        var a = 네이티브_로그인(VALID_KAKAO_TOKEN, null).body().as(KakaoNativeLoginResponse.class);
        var b = 네이티브_로그인(DIFFERENT_KAKAO_TOKEN, null).body().as(KakaoNativeLoginResponse.class);

        assertThat(a.user().email()).isNotEqualTo(b.user().email());
    }

    @Test
    @DisplayName("네이티브 로그인 - invalid 토큰은 401")
    void 네이티브_로그인_invalid_토큰() {
        ExtractableResponse<Response> response = 네이티브_로그인("invalid", null);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("네이티브 로그인 - 이메일 미동의 사용자는 403")
    void 네이티브_로그인_이메일_미동의() {
        ExtractableResponse<Response> response = 네이티브_로그인("no-email", null);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("네이티브 로그인 - kakaoAccessToken 누락은 400")
    void 네이티브_로그인_토큰_누락() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
                .when().post("/oauth2/kakao/native-login")
                .then().log().all()
                .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("재발급 - 네이티브 로그인 직후 JSON 바디로 받은 refreshToken 으로 새 accessToken 발급")
    void 재발급_바디() {
        // OAuth2TokenService.createOAuth2AccessTokenByRefreshToken 는 실제 DB 의 refreshToken 으로 동작하지만
        // OAuth2TokenProvider 가 mocked 라 만료시각/검증을 stub 으로 우회한다. 본 검증은 컨트롤러 라우팅과
        // 입력 경로(JSON 바디)가 동작함을 확인하는 데 집중한다.
        var login = 네이티브_로그인(VALID_KAKAO_TOKEN, null).body().as(KakaoNativeLoginResponse.class);

        when(oAuth2TokenProvider.createAccessTokenExpireTime()).thenReturn(
                new java.util.Date(System.currentTimeMillis() + 60_000)
        );
        when(oAuth2TokenProvider.createOAuth2AccessToken(any(), any())).thenReturn("renewed-access-token");

        var body = new ReissueTokenRequest(login.refreshToken());
        ExtractableResponse<Response> response = post("/oauth2/reissue-token", body);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        OAuth2ReIssueTokenResponse renewed = response.body().as(OAuth2ReIssueTokenResponse.class);
        assertThat(renewed.accessToken()).isEqualTo("renewed-access-token");
    }

    @Test
    @DisplayName("재발급 - 쿠키/바디 둘 다 없으면 401")
    void 재발급_입력_누락() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/oauth2/reissue-token")
                .then().log().all()
                .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("응답 바디는 쿠키 헤더 없이 JSON 으로만 전달된다 (네이티브 앱 호환)")
    void 응답에_쿠키_없음() throws Exception {
        ExtractableResponse<Response> response = 네이티브_로그인(VALID_KAKAO_TOKEN, null);

        assertAll(
                () -> assertThat(response.headers().getValues("Set-Cookie")).isEmpty(),
                () -> assertThatCode(() -> {
                    JsonNode root = objectMapper.readTree(response.asString());
                    assertThat(root.has("accessToken")).isTrue();
                    assertThat(root.has("refreshToken")).isTrue();
                }).doesNotThrowAnyException()
        );
    }

    private ExtractableResponse<Response> 네이티브_로그인(final String kakaoAccessToken, final String roomUuid) {
        String body;
        try {
            var node = objectMapper.createObjectNode();
            node.put("kakaoAccessToken", kakaoAccessToken);
            if (roomUuid != null) {
                node.put("roomUuid", roomUuid);
            }
            body = objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post("/oauth2/kakao/native-login")
                .then().log().all()
                .extract();
    }

}
