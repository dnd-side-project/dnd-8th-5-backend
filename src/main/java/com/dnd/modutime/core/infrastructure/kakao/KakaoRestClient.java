package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.auth.oauth.exception.KakaoApiException;
import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoUnlinkResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class KakaoRestClient implements KakaoClient {

    private static final String UNLINK_PATH = "/v1/user/unlink";
    private static final String USER_INFO_PATH = "/v2/user/me";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final ParameterizedTypeReference<Map<String, Object>> USER_INFO_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestTemplate restTemplate;

    public KakaoRestClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public KakaoUnlinkResponse unlink(final String targetId, final String targetIdType) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("target_id_type", targetIdType);
        body.add("target_id", targetId);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return this.restTemplate.postForObject(UNLINK_PATH, new HttpEntity<>(body, headers), KakaoUnlinkResponse.class);
    }

    /**
     * 사용자 access token 으로 /v2/user/me 를 호출한다.
     * <p>{@link com.dnd.modutime.core.infrastructure.kakao.config.KakaoRequestInterceptor} 가 admin key 를
     * 덮어쓰지 않도록 호출 시점에 Authorization 헤더를 명시 주입한다.</p>
     */
    @Override
    public Map<String, Object> getUserInfo(final String userAccessToken) {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + userAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            var response = this.restTemplate.exchange(
                    USER_INFO_PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    USER_INFO_RESPONSE_TYPE
            );
            return response.getBody();
        } catch (ResourceAccessException e) {
            // I/O 타임아웃 / 연결 실패 등
            throw new KakaoApiException("카카오 사용자 정보 조회 실패", e);
        }
    }
}
