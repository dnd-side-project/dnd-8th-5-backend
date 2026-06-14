package com.dnd.modutime.controller.admin;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.admin.application.AdminAuthService;
import com.dnd.modutime.core.admin.application.response.AdminLoginResponse;
import com.dnd.modutime.core.admin.application.response.AdminReissueTokenResponse;
import com.dnd.modutime.core.admin.controller.AdminAuthController;
import com.dnd.modutime.core.admin.exception.AdminAuthException;
import com.dnd.modutime.core.common.ErrorCode;
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
public class AdminAuthControllerDocsTest {

    @Mock
    private AdminAuthService adminAuthService;

    @InjectMocks
    private AdminAuthController controller;

    @DisplayName("어드민 로그인 - username/password 로 자체 JWT(액세스/리프레시) 발급")
    @Test
    void test01_로그인(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-post-login";

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("username").type(STRING).description("어드민 아이디"),
                fieldWithPath("password").type(STRING).description("어드민 비밀번호")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("발급된 어드민 JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpirationTime").type(STRING).description("액세스 토큰 만료 시각"),
                fieldWithPath("refreshToken").type(STRING).description("발급된 어드민 JWT 리프레시 토큰"),
                fieldWithPath("refreshTokenExpirationTime").type(STRING).description("리프레시 토큰 만료 시각")
        };

        when(adminAuthService.login(any(), any())).thenReturn(new AdminLoginResponse(
                "mock-admin-access-token",
                LocalDateTime.of(2026, 5, 12, 11, 0),
                "mock-admin-refresh-token",
                LocalDateTime.of(2026, 5, 26, 11, 0)
        ));

        String requestBody = """
                {
                  "username": "superadmin",
                  "password": "pw1234"
                }
                """;

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/admin/login")
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
                                                .description("어드민 로그인 - username/password 검증 후 access/refresh JWT 를 JSON 으로 발급")
                                                .tag("Admin-Auth")
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("어드민 로그인 실패 - 잘못된 자격증명은 401")
    @Test
    void test02_로그인_실패(RestDocumentationContextProvider contextProvider) throws Exception {
        when(adminAuthService.login(any(), any()))
                .thenThrow(new AdminAuthException("아이디 또는 비밀번호가 올바르지 않습니다.", ErrorCode.BAD_CREDENTIALS));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/admin/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"username": "superadmin", "password": "wrong"}
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @DisplayName("어드민 토큰 재발급 - refreshToken(JSON 바디)으로 새 accessToken 발급")
    @Test
    void test03_재발급(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-post-reissue-token";

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("refreshToken").type(STRING).description("로그인 시 발급받은 리프레시 토큰")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("새로 발급된 어드민 JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpirationTime").type(STRING).description("액세스 토큰 만료 시각")
        };

        when(adminAuthService.reissue(any())).thenReturn(new AdminReissueTokenResponse(
                "new-mock-admin-access-token",
                LocalDateTime.of(2026, 5, 12, 11, 15)
        ));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/admin/reissue-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"refreshToken": "mock-admin-refresh-token"}
                                        """)
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
                                                .description("어드민 토큰 재발급 - refreshToken(쿠키 또는 JSON 바디)으로 새 accessToken 발급")
                                                .tag("Admin-Auth")
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("어드민 토큰 재발급 - 웹은 refreshToken 쿠키로도 발급 가능")
    @Test
    void test04_재발급_쿠키(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-post-reissue-token-cookie";

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("새로 발급된 어드민 JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpirationTime").type(STRING).description("액세스 토큰 만료 시각")
        };

        when(adminAuthService.reissue(any())).thenReturn(new AdminReissueTokenResponse(
                "new-mock-admin-access-token",
                LocalDateTime.of(2026, 5, 12, 11, 15)
        ));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/admin/reissue-token")
                                .cookie(new Cookie("refreshToken", "mock-admin-refresh-token"))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
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
                                                .description("어드민 토큰 재발급 - refreshToken 쿠키로 새 accessToken 발급 (웹 클라이언트)")
                                                .tag("Admin-Auth")
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }
}
