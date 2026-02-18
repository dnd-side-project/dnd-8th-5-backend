package com.dnd.modutime.controller.participant;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.controller.ParticipantCommandController;
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
import org.springframework.restdocs.request.ParameterDescriptor;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class ParticipantCommandControllerDocsTest {

    @Mock
    private ParticipantFacade participantFacade;

    @InjectMocks
    private ParticipantCommandController controller;

    @DisplayName("등록 유저 방 참여 API")
    @Test
    void 등록_유저_방_참여(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "post-api-room-room-uuid-participants";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("name").type(STRING).description("참여자 표시 이름")
        };

        //language=JSON
        var requestLiteral = """
                { "name": "동호" }
                """;

        doNothing().when(participantFacade).joinAsOAuthUser(any());

        var mockOAuth2User = createMockOAuth2User();

        var documentationConfigurer = documentationConfiguration(contextProvider);
        documentationConfigurer.uris().withScheme("https").withHost(LOCALHOST).withPort(443);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
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

        mockMvc.perform(
                        post("/api/room/{roomUuid}/participants", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestLiteral)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.pathParameters(pathParameters),
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
                                                .description("등록 유저 방 참여 API")
                                                .tag("Participant")
                                                .requestFields(requestFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                );
    }

    private OAuth2User createMockOAuth2User() {
        var user = new User("테스트유저", "test@example.com", "profile.jpg", "thumb.jpg", OAuth2Provider.KAKAO);
        return new OAuth2User(user, Collections.singletonMap("id", "12345"), "id");
    }
}
