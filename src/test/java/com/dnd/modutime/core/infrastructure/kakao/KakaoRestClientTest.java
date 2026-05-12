package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.auth.oauth.exception.InvalidKakaoAccessTokenException;
import com.dnd.modutime.core.auth.oauth.exception.KakaoApiException;
import com.dnd.modutime.core.infrastructure.kakao.config.KakaoRequestInterceptor;
import com.dnd.modutime.core.infrastructure.kakao.config.KakaoResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Tag("unit")
@DisplayName("KakaoRestClient")
class KakaoRestClientTest {

    private static final String HOST = "https://kapi.kakao.com";
    private static final String ADMIN_KEY = "test-admin-key";
    private static final String USER_ACCESS_TOKEN = "user-access-token-abc";

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private KakaoRestClient client;

    @BeforeEach
    void setUp() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(HOST));
        this.restTemplate.getInterceptors().add(new KakaoRequestInterceptor(ADMIN_KEY));
        this.restTemplate.setErrorHandler(new KakaoResponseHandler(new ObjectMapper()));
        this.server = MockRestServiceServer.createServer(this.restTemplate);
        this.client = new KakaoRestClient(this.restTemplate);
    }

    @Test
    @DisplayName("unlink 호출 시 KakaoAK 헤더와 form 파라미터로 POST하고 응답을 매핑한다")
    void unlink_정상호출() {
        var expected = new LinkedMultiValueMap<String, String>();
        expected.add("target_id_type", "user_id");
        expected.add("target_id", "123456");

        this.server.expect(requestTo(HOST + "/v1/user/unlink"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "KakaoAK " + ADMIN_KEY))
                .andExpect(content().formData(expected))
                .andRespond(withSuccess("{\"id\":123456}", MediaType.APPLICATION_JSON));

        var response = client.unlink("123456", "user_id");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(123456L);
        this.server.verify();
    }

    @Test
    @DisplayName("unlinkByUserId는 target_id_type=user_id 로 호출한다")
    void unlinkByUserId() {
        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
        expected.add("target_id_type", "user_id");
        expected.add("target_id", "777");

        this.server.expect(requestTo(HOST + "/v1/user/unlink"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(expected))
                .andRespond(withSuccess("{\"id\":777}", MediaType.APPLICATION_JSON));

        var response = client.unlinkByUserId("777");

        assertThat(response.id()).isEqualTo(777L);
        this.server.verify();
    }

    @Test
    @DisplayName("getUserInfo 는 사용자 access token 으로 Bearer 헤더를 붙여 /v2/user/me 를 호출한다 (admin key 미사용)")
    void getUserInfo_정상호출() {
        String responseBody = """
                {
                  "id": 12345,
                  "properties": { "nickname": "테스터", "profile_image": "p.jpg", "thumbnail_image": "t.jpg" },
                  "kakao_account": { "email": "user@example.com" }
                }
                """;

        this.server.expect(requestTo(HOST + "/v2/user/me"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_ACCESS_TOKEN))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Map<String, Object> result = client.getUserInfo(USER_ACCESS_TOKEN);

        assertThat(result).isNotNull();
        assertThat(result.get("id")).isEqualTo(12345);
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) result.get("kakao_account");
        assertThat(account.get("email")).isEqualTo("user@example.com");
        this.server.verify();
    }

    @Test
    @DisplayName("getUserInfo 401 응답이면 InvalidKakaoAccessTokenException 을 던진다")
    void getUserInfo_401() {
        this.server.expect(requestTo(HOST + "/v2/user/me"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
                        .body("{\"msg\":\"invalid token\",\"code\":-401}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getUserInfo(USER_ACCESS_TOKEN))
                .isInstanceOf(InvalidKakaoAccessTokenException.class);
        this.server.verify();
    }

    @Test
    @DisplayName("getUserInfo 5xx 응답이면 KakaoApiException 을 던진다")
    void getUserInfo_5xx() {
        this.server.expect(requestTo(HOST + "/v2/user/me"))
                .andRespond(withServerError()
                        .body("{\"msg\":\"down\",\"code\":-500}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getUserInfo(USER_ACCESS_TOKEN))
                .isInstanceOf(KakaoApiException.class);
        this.server.verify();
    }
}
