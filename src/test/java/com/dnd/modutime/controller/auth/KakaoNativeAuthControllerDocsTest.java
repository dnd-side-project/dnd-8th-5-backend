package com.dnd.modutime.controller.auth;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.controller.KakaoNativeAuthController;
import com.dnd.modutime.core.auth.oauth.controller.dto.KakaoNativeLoginResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.UserSummary;
import com.dnd.modutime.core.auth.oauth.exception.InvalidKakaoAccessTokenException;
import com.dnd.modutime.core.auth.oauth.exception.KakaoApiException;
import com.dnd.modutime.core.auth.oauth.exception.KakaoEmailNotProvidedException;
import com.dnd.modutime.core.auth.oauth.facade.KakaoNativeAuthFacade;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.time.LocalDateTime;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class KakaoNativeAuthControllerDocsTest {

    @Mock
    private KakaoNativeAuthFacade facade;

    @InjectMocks
    private KakaoNativeAuthController controller;

    @DisplayName("네이티브 카카오 로그인 - access token 으로 자체 JWT 발급")
    @Test
    void test01_정상_로그인(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "oauth2-post-kakao-native-login";

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("kakaoAccessToken").type(STRING).description("카카오 SDK 가 발급한 사용자 access token"),
                fieldWithPath("roomUuid").type(STRING).optional().description("로그인 직후 진입할 방 UUID (선택)")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("발급된 자체 JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpirationTime").type(STRING).description("액세스 토큰 만료 시각"),
                fieldWithPath("refreshToken").type(STRING).description("발급된 자체 JWT 리프레시 토큰"),
                fieldWithPath("refreshTokenExpirationTime").type(STRING).description("리프레시 토큰 만료 시각"),
                fieldWithPath("user.name").type(STRING).description("사용자 이름"),
                fieldWithPath("user.email").type(STRING).description("사용자 이메일"),
                fieldWithPath("user.profileImage").type(STRING).description("프로필 이미지 URL"),
                fieldWithPath("user.thumbnailImage").type(STRING).description("썸네일 이미지 URL"),
                fieldWithPath("roomUuid").type(STRING).optional().description("요청 시 전달된 방 UUID echo")
        };

        when(facade.login(any())).thenReturn(new KakaoNativeLoginResponse(
                "mock-access-token-abc",
                LocalDateTime.of(2026, 5, 12, 11, 0),
                "mock-refresh-token-xyz",
                LocalDateTime.of(2026, 5, 26, 11, 0),
                new UserSummary("동호", "user@example.com",
                        "https://example.com/p.jpg", "https://example.com/t.jpg"),
                "550e8400-e29b-41d4-a716-446655440000"
        ));

        String requestBody = """
                {
                  "kakaoAccessToken": "kakao-sdk-access-token",
                  "roomUuid": "550e8400-e29b-41d4-a716-446655440000"
                }
                """;

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/oauth2/kakao/native-login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                PayloadDocumentation.requestFields(requestFields),
                                PayloadDocumentation.responseFields(responseFields)
                        )
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("네이티브 앱에서 카카오 SDK access token 으로 자체 JWT 발급 (쿠키 없이 JSON 응답)")
                                                .tag("Auth-OAuth2")
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("네이티브 카카오 로그인 실패 - 잘못된 카카오 토큰")
    @Test
    void test02_invalid_token(RestDocumentationContextProvider contextProvider) throws Exception {
        when(facade.login(any())).thenThrow(new InvalidKakaoAccessTokenException("유효하지 않은 카카오 토큰입니다."));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/oauth2/kakao/native-login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"kakaoAccessToken": "invalid"}
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @DisplayName("네이티브 카카오 로그인 실패 - 카카오 이메일 미동의")
    @Test
    void test03_email_not_provided(RestDocumentationContextProvider contextProvider) throws Exception {
        when(facade.login(any())).thenThrow(
                new KakaoEmailNotProvidedException("카카오 계정 이메일 제공에 동의해야 로그인할 수 있습니다."));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/oauth2/kakao/native-login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"kakaoAccessToken": "no-email-token"}
                                        """)
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @DisplayName("네이티브 카카오 로그인 실패 - 카카오 API 오류 (5xx)")
    @Test
    void test04_kakao_api_error(RestDocumentationContextProvider contextProvider) throws Exception {
        when(facade.login(any())).thenThrow(new KakaoApiException("카카오 API 호출에 실패했습니다."));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/oauth2/kakao/native-login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"kakaoAccessToken": "any"}
                                        """)
                )
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}
