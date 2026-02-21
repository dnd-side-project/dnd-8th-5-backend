package com.dnd.modutime.controller.participant;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.annotation.WithMockRoomParticipant;
import com.dnd.modutime.core.participant.controller.ParticipantQueryController;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class ParticipantQueryControllerDocsTest {

    private final ParticipantQueryController controller = new ParticipantQueryController();

    @DisplayName("내 참여 정보 조회 API")
    @WithMockRoomParticipant
    @Test
    void 내_참여_정보_조회(RestDocumentationContextProvider contextProvider,
                    HandlerMethodArgumentResolver roomParticipantResolver) throws Exception {
        var operationIdentifier = "get-api-v1-rooms-room-uuid-participants-me";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("name").type(STRING).description("참여자 이름")
        };

        var mockMvc = MockMvcFactory.getRestDocsMockMvc(
                contextProvider, LOCALHOST,
                new HandlerMethodArgumentResolver[]{roomParticipantResolver},
                controller);

        mockMvc.perform(
                        get("/api/v1/rooms/{roomUuid}/participants/me", "test-room-uuid")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.pathParameters(pathParameters),
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
                                                .description("내 참여 정보 조회 API")
                                                .tag("Participant")
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                );
    }
}
