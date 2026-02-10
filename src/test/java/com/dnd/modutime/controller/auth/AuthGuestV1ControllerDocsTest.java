package com.dnd.modutime.controller.auth;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.application.GuestAuthFacade;
import com.dnd.modutime.core.auth.application.response.GuestLoginResponse;
import com.dnd.modutime.core.auth.controller.AuthGuestController;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.room.application.RoomService;
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
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;

import java.time.LocalDateTime;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class AuthGuestV1ControllerDocsTest {

    @Mock
    private ParticipantFacade participantFacade;

    @Mock
    private RoomService roomService;

    @Mock
    private GuestAuthFacade guestAuthFacade;

    @InjectMocks
    private AuthGuestController controller;

    @DisplayName("Guest V1 로그인 API - JWT 토큰 반환")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-post-api-v1-room-room-uuid-login";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("name").type(STRING).description("참여자 이름"),
                fieldWithPath("password").type(STRING).description("비밀번호")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("accessToken").type(STRING).description("JWT 액세스 토큰"),
                fieldWithPath("accessTokenExpireTime").type(STRING).description("액세스 토큰 만료 시간")
        };

        //language=JSON
        var requestLiteral = """
                {
                  "name": "동호",
                  "password": "1234"
                }
                """;

        when(guestAuthFacade.login(any())).thenReturn(
                new GuestLoginResponse("mock-jwt-token", LocalDateTime.now().plusHours(1))
        );

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/guest/api/v1/room/{roomUuid}/login", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestLiteral)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo( //Spring REST Docs
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.pathParameters(pathParameters),
                                PayloadDocumentation.requestFields(requestFields),
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
                                                .description("Guest V1 로그인 API - JWT 토큰 반환")
                                                .tag("Auth-Guest")
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                )
        ;
    }
}
