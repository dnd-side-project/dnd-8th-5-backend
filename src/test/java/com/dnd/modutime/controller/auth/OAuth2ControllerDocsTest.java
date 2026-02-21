package com.dnd.modutime.controller.auth;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.controller.OAuth2Controller;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2LoginResponse;
import com.dnd.modutime.core.auth.oauth.controller.dto.OAuth2ReIssueTokenResponse;
import com.dnd.modutime.core.auth.oauth.facade.OAuth2TokenService;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class OAuth2ControllerDocsTest {

    @Mock
    private OAuth2TokenService oAuth2TokenService;

    @InjectMocks
    private OAuth2Controller controller;

    /**
     * OAuth2 로그인 엔드포인트 문서화를 위한 Mock Controller
     * Spring Security가 실제 로그인을 처리하므로, 문서화 목적으로만 사용
     */
    @RestController
    private static class MockOAuth2LoginController {
        @GetMapping("/oauth2/authorization/{registrationId}")
        public OAuth2LoginResponse oAuth2Login(
                @PathVariable String registrationId,
                @RequestParam(value = "roomUuid", required = false) String roomUuid
        ) {
            return new OAuth2LoginResponse(
                    "mock-access-token-abc123xyz",
                    LocalDateTime.now().plusHours(1),
                    "mock-refresh-token-def456uvw",
                    LocalDateTime.now().plusDays(7)
            );
        }
    }

    @DisplayName("OAuth2 토큰 재발급 API - refreshToken 쿠키로 새 accessToken 발급")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "oauth2-post-reissue-token";

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("새로 발급된 JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpirationTime").type(STRING).description("액세스 토큰 만료 시간")
        };

        when(oAuth2TokenService.createOAuth2AccessTokenByRefreshToken(any())).thenReturn(
                new OAuth2ReIssueTokenResponse("new-mock-access-token", LocalDateTime.now().plusHours(1))
        );

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/oauth2/reissue-token")
                                .cookie(new Cookie("refreshToken", "mock-refresh-token"))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo( // Spring REST Docs
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                PayloadDocumentation.responseFields(responseFields)
                        )
                )
                .andDo( // Spring REST Docs to OpenAPI
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("OAuth2 토큰 재발급 API - refreshToken 쿠키로 새 accessToken 발급")
                                                .tag("Auth-OAuth2")
                                                .responseFields(responseFields)
                                                .build())
                        )
                )
        ;
    }

    @DisplayName("OAuth2 로그인 API - OAuth2 Provider 로그인 페이지로 리다이렉트")
    @Test
    void test02(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "oauth2-get-authorization-registration-id";

        var pathParams = new ParameterDescriptor[]{
                parameterWithName("registrationId").description("OAuth2 Provider ID (예: kakao)")
        };

        var requestParams = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID (선택사항)").optional()
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("발급된 JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpireTime").type(STRING).description("액세스 토큰 만료 시간"),
                fieldWithPath("refreshToken").type(STRING).description("발급된 JWT 리프레시 토큰"),
                fieldWithPath("refreshTokenExpireTime").type(STRING).description("리프레시 토큰 만료 시간")
        };

        var mockController = new MockOAuth2LoginController();

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, mockController)
                .perform(
                        get("/oauth2/authorization/{registrationId}", "kakao")
                                .param("roomUuid", "550e8400-e29b-41d4-a716-446655440000")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo( // Spring REST Docs
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                pathParameters(pathParams),
                                requestParameters(requestParams),
                                PayloadDocumentation.responseFields(responseFields)
                        )
                )
                .andDo( // Spring REST Docs to OpenAPI
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("OAuth2 로그인 API - OAuth2 Provider 로그인 페이지로 리다이렉트")
                                                .tag("Auth-OAuth2")
                                                .pathParameters(pathParams)
                                                .requestParameters(requestParams)
                                                .responseFields(responseFields)
                                                .build())
                        )
                )
        ;
    }
}
