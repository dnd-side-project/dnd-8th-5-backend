package com.dnd.modutime.core.infrastructure.kakao.config;

import com.dnd.modutime.core.infrastructure.kakao.KakaoException;
import com.dnd.modutime.core.infrastructure.kakao.config.dto.KakaoErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * 카카오 응답 에러 핸들러.
 * <ul>
 *   <li>400 + code=-101 (이미 연결되지 않음): 멱등 처리 — swallow</li>
 *   <li>4xx → {@link KakaoException.KakaoClientException}</li>
 *   <li>5xx → {@link KakaoException.KakaoServerException}</li>
 * </ul>
 */
@Slf4j
public class KakaoResponseHandler implements ResponseErrorHandler {

    private static final String ALREADY_UNLINKED_CODE = "-101";

    private final ObjectMapper objectMapper;

    public KakaoResponseHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        var status = response.getStatusCode();
        KakaoErrorResponse body = parseBodySafely(response);

        log.error("[KakaoClient] response error: status={}, body={}", status, body);

        if (status == HttpStatus.BAD_REQUEST && body != null && ALREADY_UNLINKED_CODE.equals(body.code())) {
            // 이미 연결 해제된 상태로 간주하고 정상 처리
            return;
        }

        var message = (body != null && body.message() != null) ? body.message() : status.getReasonPhrase();

        if (status.is4xxClientError()) {
            throw new KakaoException.KakaoClientException(message);
        }
        if (status.is5xxServerError()) {
            throw new KakaoException.KakaoServerException(message);
        }
        throw new KakaoException(message);
    }

    private KakaoErrorResponse parseBodySafely(final ClientHttpResponse response) {
        try {
            return this.objectMapper.readValue(response.getBody(), KakaoErrorResponse.class);
        } catch (IOException e) {
            log.warn("[KakaoClient] error response body parsing failed", e);
            return null;
        }
    }
}
