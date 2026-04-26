package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.kakao.config.KakaoRequestInterceptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Tag("unit")
@DisplayName("KakaoRestClient")
class KakaoRestClientTest {

    private static final String HOST = "https://kapi.kakao.com";
    private static final String ADMIN_KEY = "test-admin-key";

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private KakaoRestClient client;

    @BeforeEach
    void setUp() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(HOST));
        this.restTemplate.getInterceptors().add(new KakaoRequestInterceptor(ADMIN_KEY));
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
}
