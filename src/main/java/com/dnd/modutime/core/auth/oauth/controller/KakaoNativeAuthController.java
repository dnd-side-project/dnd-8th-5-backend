package com.dnd.modutime.core.auth.oauth.controller;

import com.dnd.modutime.core.auth.oauth.controller.dto.KakaoNativeLoginRequest;
import com.dnd.modutime.core.auth.oauth.controller.dto.KakaoNativeLoginResponse;
import com.dnd.modutime.core.auth.oauth.facade.KakaoNativeAuthFacade;
import com.dnd.modutime.core.auth.oauth.facade.command.KakaoNativeLoginCommand;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 네이티브 앱 (iOS/Android) 용 카카오 로그인 컨트롤러.
 *
 * <p>웹 흐름의 OAuth2 Authorization Code 리다이렉트 + 쿠키 패턴을 사용할 수 없는 네이티브 환경을 위한
 * 별도 엔드포인트. 카카오 SDK 가 발급한 사용자 access token 을 받아 자체 JWT (access + refresh)를
 * JSON 바디로 발급한다.</p>
 */
@RestController
public class KakaoNativeAuthController {

    private final KakaoNativeAuthFacade facade;

    public KakaoNativeAuthController(final KakaoNativeAuthFacade facade) {
        this.facade = facade;
    }

    /**
     * 카카오 SDK access token 으로 네이티브 로그인을 수행한다.
     *
     * <p>요청에 포함된 카카오 access token 은 서버가 카카오 /v2/user/me 로 검증하고, 사용자 정보를 받아
     * 자체 사용자로 매핑(신규 가입 또는 기존 사용자 재로그인)한 뒤 자체 access/refresh 토큰을 발급한다.</p>
     *
     * <p>응답은 JSON 바디로만 전달되며 쿠키는 설정되지 않는다. 클라이언트는 Keychain/EncryptedSharedPreferences
     * 등 안전한 저장소에 refresh token 을 보관해야 한다.</p>
     */
    @PostMapping("/oauth2/kakao/native-login")
    public KakaoNativeLoginResponse nativeLogin(@RequestBody @Valid final KakaoNativeLoginRequest request) {
        return this.facade.login(KakaoNativeLoginCommand.of(request.kakaoAccessToken(), request.roomUuid()));
    }
}
