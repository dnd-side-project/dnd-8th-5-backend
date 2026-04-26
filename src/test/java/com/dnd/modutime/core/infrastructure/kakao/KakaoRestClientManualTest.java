package com.dnd.modutime.core.infrastructure.kakao;

import com.dnd.modutime.core.infrastructure.common.CommonClientHttpRequestFactory;
import com.dnd.modutime.core.infrastructure.kakao.config.KakaoRequestInterceptor;
import com.dnd.modutime.core.infrastructure.kakao.config.KakaoResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;

/**
 * 실제 카카오 API에 unlink 요청을 보내는 수동 검증용 테스트.
 *
 * <p><b>이 테스트는 기본적으로 비활성화되어 있다.</b> 실제 카카오 API를 호출하므로,
 * 운영 사용자에게 영향을 줄 수 있어 CI에서 자동 실행되어서는 안 된다.</p>
 *
 * <h3>사용 방법</h3>
 * <ol>
 *   <li>카카오 디벨로퍼스 콘솔에서 어드민 키 발급 (앱 키 → Admin 키)</li>
 *   <li>아래 {@code ADMIN_KEY}와 {@code TARGET_ID}를 본인 값으로 교체</li>
 *   <li>{@link Disabled} 어노테이션을 임시로 주석 처리</li>
 *   <li>{@code ./gradlew test --tests "*KakaoRestClientManualTest*"} 실행</li>
 *   <li>검증 끝나면 반드시 {@link Disabled}를 다시 활성화하고 키/ID 값은 원복</li>
 * </ol>
 *
 * <h3>주의</h3>
 * <ul>
 *   <li>해당 카카오 계정과 앱의 연결이 실제로 해제됨 (재로그인 필요)</li>
 *   <li>어드민 키는 절대 커밋하지 말 것 — 변수에 직접 넣었다가 원복</li>
 *   <li>카카오는 어드민 키에 IP 화이트리스트 정책을 적용할 수 있으므로,
 *       본 테스트가 실패하면 콘솔의 플랫폼 IP 등록 확인</li>
 * </ul>
 */
@Tag("manual")
@Disabled("실제 카카오 API 호출 — 검증할 때만 수동 활성화")
@DisplayName("KakaoRestClient 수동 통합 테스트")
class KakaoRestClientManualTest {

    /** 카카오 디벨로퍼스 콘솔 → 앱 키 → Admin 키 */
    private static final String ADMIN_KEY = "PUT_YOUR_ADMIN_KEY_HERE";

    /** 연결 해제할 카카오 사용자 ID — users.oauth_id 또는 카카오 콘솔에서 확인 */
    private static final String TARGET_ID = "PUT_YOUR_KAKAO_USER_ID_HERE";

    private static final String KAKAO_HOST = "https://kapi.kakao.com";

    @Test
    @DisplayName("실제 카카오 API에 unlink 호출 → 응답 형식과 라이브 동작 확인")
    void 실제_카카오_unlink() {
        var requestFactory = CommonClientHttpRequestFactory.create(Duration.ofSeconds(3), Duration.ofSeconds(5));
        var restTemplate = new RestTemplateBuilder()
                .rootUri(KAKAO_HOST)
                .requestFactory(() -> requestFactory)
                .additionalInterceptors(new KakaoRequestInterceptor(ADMIN_KEY))
                .errorHandler(new KakaoResponseHandler(new ObjectMapper()))
                .build();

        var client = new KakaoRestClient(restTemplate);

        var response = client.unlinkByUserId(TARGET_ID);

        // 정상 응답: { "id": <kakao user id> }
        System.out.println("[KakaoRestClientManualTest] response = " + response);

        // 검증할 것:
        //  1) response.id() 가 TARGET_ID와 일치하는가? (Long 변환 검증)
        //  2) 카카오 콘솔 "사용자 관리"에서 해당 사용자의 앱 연결이 끊겼는가?
        //  3) 잘못된 키로 호출하면 KakaoClientException, 5xx면 KakaoServerException 인가?
        //  4) 이미 끊긴 사용자에 대해 다시 호출하면 -101 멱등 처리되는가?
    }
}
