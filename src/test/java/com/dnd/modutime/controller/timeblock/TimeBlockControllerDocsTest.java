package com.dnd.modutime.controller.timeblock;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.annotation.WithMockRoomParticipant;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.controller.TimeBlockController;
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
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import com.dnd.modutime.documentation.TestJsonUtils;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static com.dnd.modutime.documentation.MockMvcFactory.HEADER_AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class TimeBlockControllerDocsTest {

    @Mock
    private TimeBlockService timeBlockService;

    @InjectMocks
    private TimeBlockController controller;

    @DisplayName("참여자 시간 등록 API")
    @WithMockRoomParticipant
    @Test
    void 참여자_시간_등록(RestDocumentationContextProvider contextProvider,
                   HandlerMethodArgumentResolver roomParticipantResolver) throws Exception {
        var operationIdentifier = "put-api-v1-rooms-room-uuid-time-blocks-available-time";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var requestHeaders = new HeaderDescriptor[]{
                headerWithName("Authorization").description("인증 토큰 (Bearer {JWT-TOKEN})")
        };

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("hasTime").type(BOOLEAN).description("시간 포함 여부"),
                fieldWithPath("availableDateTimes").type(ARRAY).description("가능한 날짜/시간 목록 (형식: yyyy-MM-dd HH:mm)")
        };

        //language=JSON
        var requestLiteral = """
                {
                  "hasTime": true,
                  "availableDateTimes": [
                    "2025-07-16 13:00",
                    "2025-07-16 13:30",
                    "2025-07-16 14:00"
                  ]
                }
                """;

        doNothing().when(timeBlockService).replaceV1(any());

        var mockMvc = MockMvcFactory.getRestDocsMockMvc(
                contextProvider, LOCALHOST,
                new HandlerMethodArgumentResolver[]{roomParticipantResolver},
                controller);

        mockMvc.perform(
                        put("/api/v1/rooms/{roomUuid}/time-blocks/available-time", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", HEADER_AUTHORIZATION)
                                .content(requestLiteral)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                HeaderDocumentation.requestHeaders(requestHeaders),
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
                                                .description("참여자 시간 등록 API")
                                                .tag("TimeBlock")
                                                .requestHeaders(requestHeaders)
                                                .requestFields(requestFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                );
    }

    @DisplayName("참여자 시간 조회 API")
    @WithMockRoomParticipant
    @Test
    void 참여자_시간_조회(RestDocumentationContextProvider contextProvider,
                   HandlerMethodArgumentResolver roomParticipantResolver) throws Exception {
        var operationIdentifier = "get-api-v1-rooms-room-uuid-available-time";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var requestHeaders = new HeaderDescriptor[]{
                headerWithName("Authorization").description("인증 토큰 (Bearer {JWT-TOKEN})")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("name").type(STRING).description("참여자 이름"),
                fieldWithPath("availableDateTimes").type(ARRAY).description("등록한 날짜/시간 목록 (형식: yyyy-MM-dd HH:mm)")
        };

        //language=JSON
        var responseLiteral = """
                {
                  "name": "동호",
                  "availableDateTimes": [
                    "2025-07-16 13:00",
                    "2025-07-16 13:30",
                    "2025-07-16 14:00"
                  ]
                }
                """;

        var response = TestJsonUtils.readValue(responseLiteral, TimeBlockResponse.class);
        when(timeBlockService.getTimeBlock(any(), any())).thenReturn(response);

        var mockMvc = MockMvcFactory.getRestDocsMockMvc(
                contextProvider, LOCALHOST,
                new HandlerMethodArgumentResolver[]{roomParticipantResolver},
                controller);

        mockMvc.perform(
                        get("/api/v1/rooms/{roomUuid}/available-time", "test-room-uuid")
                                .header("Authorization", HEADER_AUTHORIZATION)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                HeaderDocumentation.requestHeaders(requestHeaders),
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
                                                .description("참여자 시간 조회 API")
                                                .tag("TimeBlock")
                                                .requestHeaders(requestHeaders)
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                );
    }
}
