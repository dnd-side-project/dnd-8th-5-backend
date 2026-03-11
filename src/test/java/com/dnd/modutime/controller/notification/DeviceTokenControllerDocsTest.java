package com.dnd.modutime.controller.notification;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.notification.application.DeviceTokenService;
import com.dnd.modutime.core.notification.controller.DeviceTokenController;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.documentation.DocumentUtils;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class DeviceTokenControllerDocsTest {

    @Mock
    private DeviceTokenService deviceTokenService;

    @InjectMocks
    private DeviceTokenController controller;

    @DisplayName("디바이스 토큰 등록 API")
    @Test
    void 디바이스_토큰_등록(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "post-api-v1-device-tokens";

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("token").type(STRING).description("FCM 등록 토큰")
        };

        //language=JSON
        var requestLiteral = """
                { "token": "fcm-registration-token-string" }
                """;

        when(deviceTokenService.register(any())).thenReturn(true);

        var mockMvc = createMockMvc(contextProvider);

        mockMvc.perform(
                        post("/api/v1/device-tokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestLiteral)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                PayloadDocumentation.requestFields(requestFields)
                        )
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("디바이스 토큰 등록 API")
                                                .tag("DeviceToken")
                                                .requestFields(requestFields)
                                                .build())
                        )
                );
    }

    @DisplayName("디바이스 토큰 해제 API")
    @Test
    void 디바이스_토큰_해제(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "delete-api-v1-device-tokens";

        var mockMvc = createMockMvc(contextProvider);

        mockMvc.perform(
                        delete("/api/v1/device-tokens")
                                .param("token", "fcm-registration-token-string")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.requestParameters(
                                        RequestDocumentation.parameterWithName("token").description("FCM 등록 토큰")
                                )
                        )
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("디바이스 토큰 해제 API")
                                                .tag("DeviceToken")
                                                .requestParameters(
                                                        ResourceDocumentation.parameterWithName("token").description("FCM 등록 토큰")
                                                )
                                                .build())
                        )
                );
    }

    private MockMvc createMockMvc(RestDocumentationContextProvider contextProvider) {
        var mockOAuth2User = createMockOAuth2User();
        var documentationConfigurer = documentationConfiguration(contextProvider);
        documentationConfigurer.uris().withScheme("https").withHost(LOCALHOST).withPort(443);

        return MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter,
                                                  ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest,
                                                  WebDataBinderFactory binderFactory) {
                        return mockOAuth2User;
                    }
                })
                .apply(documentationConfigurer)
                .build();
    }

    private OAuth2User createMockOAuth2User() {
        var user = new User("테스트유저", "test@example.com", "profile.jpg", "thumb.jpg", OAuth2Provider.KAKAO);
        return new OAuth2User(user, Collections.singletonMap("id", "12345"), "id");
    }
}
