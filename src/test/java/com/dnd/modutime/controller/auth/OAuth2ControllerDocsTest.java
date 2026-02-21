package com.dnd.modutime.controller.auth;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.controller.OAuth2Controller;
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

import javax.servlet.http.Cookie;
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
public class OAuth2ControllerDocsTest {

    @Mock
    private OAuth2TokenService oAuth2TokenService;

    @InjectMocks
    private OAuth2Controller controller;

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
}
