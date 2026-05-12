package com.dnd.modutime.core.infrastructure.kakao.config;

import com.dnd.modutime.core.auth.oauth.exception.InvalidKakaoAccessTokenException;
import com.dnd.modutime.core.auth.oauth.exception.KakaoApiException;
import com.dnd.modutime.core.infrastructure.kakao.KakaoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("KakaoResponseHandler")
class KakaoResponseHandlerTest {

    private KakaoResponseHandler handler;

    @BeforeEach
    void setUp() {
        this.handler = new KakaoResponseHandler(new ObjectMapper());
    }

    @Test
    @DisplayName("정상 응답이면 hasError는 false를 반환한다")
    void hasError_정상응답_false() throws Exception {
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.OK);

        var result = handler.hasError(response);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("4xx 응답이면 hasError는 true를 반환한다")
    void hasError_4xx응답_true() throws Exception {
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST);

        var result = handler.hasError(response);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("400 + code -101 (이미 미연결)이면 멱등 처리하여 예외를 던지지 않는다")
    void handleError_이미_미연결_예외없음() {
        var body = "{\"msg\":\"not linked\",\"code\":-101}".getBytes(StandardCharsets.UTF_8);
        var response = new MockClientHttpResponse(body, HttpStatus.BAD_REQUEST);

        assertThatCode(() -> handler.handleError(response)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("400 응답은 KakaoClientException을 던진다")
    void handleError_400_KakaoClientException() {
        var body = "{\"msg\":\"IllegalParam\",\"code\":-2}".getBytes(StandardCharsets.UTF_8);
        var response = new MockClientHttpResponse(body, HttpStatus.BAD_REQUEST);

        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(KakaoException.KakaoClientException.class)
                .hasMessageContaining("IllegalParam");
    }

    @Test
    @DisplayName("401 응답은 InvalidKakaoAccessTokenException을 던진다")
    void handleError_401_InvalidKakaoAccessToken() {
        var body = "{\"msg\":\"unauthorized\",\"code\":-401}".getBytes(StandardCharsets.UTF_8);
        var response = new MockClientHttpResponse(body, HttpStatus.UNAUTHORIZED);

        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(InvalidKakaoAccessTokenException.class)
                .hasMessageContaining("unauthorized");
    }

    @Test
    @DisplayName("403 응답도 InvalidKakaoAccessTokenException을 던진다")
    void handleError_403_InvalidKakaoAccessToken() {
        var body = "{\"msg\":\"forbidden\",\"code\":-403}".getBytes(StandardCharsets.UTF_8);
        var response = new MockClientHttpResponse(body, HttpStatus.FORBIDDEN);

        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(InvalidKakaoAccessTokenException.class);
    }

    @Test
    @DisplayName("5xx 응답은 KakaoApiException을 던진다")
    void handleError_5xx_KakaoApiException() {
        var body = "{\"msg\":\"internal\",\"code\":-500}".getBytes(StandardCharsets.UTF_8);
        var response = new MockClientHttpResponse(body, HttpStatus.INTERNAL_SERVER_ERROR);

        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(KakaoApiException.class)
                .hasMessageContaining("internal");
    }

    @Test
    @DisplayName("응답 바디 파싱 실패 시에도 status 기반으로 적절한 예외를 던진다")
    void handleError_바디파싱실패_상태기반예외() {
        var invalidBody = "not-json".getBytes(StandardCharsets.UTF_8);
        var response = new MockClientHttpResponse(invalidBody, HttpStatus.SERVICE_UNAVAILABLE);

        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(KakaoApiException.class);
    }
}
