package com.dnd.modutime.controller.auth;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.application.response.LoginPageResponse;
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

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class AuthGuestControllerDocsTest {

    @Mock
    private ParticipantFacade participantFacade;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private AuthGuestController controller;

    @DisplayName("Guest 로그인 API")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-post-api-room-room-uuid-login";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("name").type(STRING).description("참여자 이름"),
                fieldWithPath("password").type(STRING).description("비밀번호")
        };

        //language=JSON
        var requestLiteral = """
                {
                  "name": "동호",
                  "password": "1234"
                }
                """;

        doNothing().when(participantFacade).login(any());

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/guest/api/room/{roomUuid}/login", "test-room-uuid")
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
                                PayloadDocumentation.requestFields(requestFields)
                        )
                )
                .andDo( // Spring REST Docs to OpenAPI
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("Guest용 로그인 API")
                                                .tag("Auth-Guest")
                                                .requestFields(requestFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                )
        ;
    }

    @DisplayName("Guest 로그인 페이지 정보 조회 API")
    @Test
    void test02(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-get-api-room-room-uuid-login";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("roomTitle").type(STRING).description("방 제목")
        };

        when(roomService.getTitleByUuid(any())).thenReturn("모두의 회의");

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/guest/api/room/{roomUuid}/login", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo( //Spring REST Docs
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.pathParameters(pathParameters),
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
                                                .description("Guest용 로그인 페이지 정보 조회 API")
                                                .tag("Auth-Guest")
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                )
        ;
    }
}
