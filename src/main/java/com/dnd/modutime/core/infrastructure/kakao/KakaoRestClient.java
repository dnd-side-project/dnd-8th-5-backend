package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoUnlinkResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

public class KakaoRestClient implements KakaoClient {

    private static final String UNLINK_PATH = "/v1/user/unlink";

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
}
