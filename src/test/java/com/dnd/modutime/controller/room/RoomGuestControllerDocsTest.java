package com.dnd.modutime.controller.room;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.V2RoomInfoResponse;
import com.dnd.modutime.core.room.controller.RoomGuestController;
import com.dnd.modutime.core.timetable.application.TimeTableService;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.dnd.modutime.documentation.TestJsonUtils;
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
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class RoomGuestControllerDocsTest {

    @Mock
    private RoomService roomService;

    @Mock
    private TimeTableService timeTableService;

    @Mock
    private AdjustmentResultService adjustmentResultService;

    @InjectMocks
    private RoomGuestController controller;

    @DisplayName("Guest 방 생성 API")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-post-api-room";

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("title").type(STRING).description("방 제목"),
                fieldWithPath("roomDates").type(ARRAY).description("방 날짜 목록"),
                fieldWithPath("roomDates[].availableDate").type(STRING).description("날짜"),
                fieldWithPath("roomDates[].availableTimes").type(ARRAY).description("시간 목록").optional(),
                fieldWithPath("roomDates[].availableTimes[].startTime").type(STRING).description("시작 시간").optional(),
                fieldWithPath("roomDates[].availableTimes[].endTime").type(STRING).description("종료 시간").optional(),
                fieldWithPath("headcount").type(NUMBER).description("예상 인원"),
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("roomUuid").type(STRING).description("방 UUID"),
        };

        //language=JSON
        var requestLiteral = """
                {
                  "title": "모두의 회의",
                  "roomDates": [
                    {
                      "availableDate": "2025-07-16",
                      "availableTimes": [
                        {
                          "startTime": "13:00",
                          "endTime": "15:00"
                        }
                      ]
                    }
                  ],
                  "headcount": 5
                }
                """;

        //language=JSON
        var responseLiteral = """
                {
                  "roomUuid": "test-room-uuid"
                }
                """;

        var response = TestJsonUtils.readValue(responseLiteral, RoomCreationResponse.class);
        when(roomService.create(any())).thenReturn(response);
        doNothing().when(timeTableService).create(any());
        doNothing().when(adjustmentResultService).create(any());

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/guest/api/room")
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
                                                .description("Guest용 방 생성 API")
                                                .tag("Room-Guest")
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .build())
                        )
                )
        ;
    }

    @DisplayName("Guest 방 정보 조회 API (V2)")
    @Test
    void test02(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-get-api-v2-room-room-uuid";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("title").type(STRING).description("방 제목"),
                fieldWithPath("deadLine").type(STRING).description("마감 일시").optional(),
                fieldWithPath("headCount").type(NUMBER).description("예상 인원").optional(),
                fieldWithPath("participants").type(ARRAY).description("참여자 목록").optional(),
                fieldWithPath("participants[].id").type(NUMBER).description("참여자 ID").optional(),
                fieldWithPath("participants[].name").type(STRING).description("참여자 이름").optional(),
                fieldWithPath("dates").type(ARRAY).description("방 날짜 목록").optional(),
                fieldWithPath("startTime").type(STRING).description("시작 시간").optional(),
                fieldWithPath("endTime").type(STRING).description("종료 시간").optional(),
        };

        //language=JSON
        var responseLiteral = """
                {
                  "title": "모두의 회의",
                  "deadLine": null,
                  "headCount": null,
                  "participants": null,
                  "dates": null,
                  "startTime": null,
                  "endTime": null
                }
                """;

        var response = TestJsonUtils.readValue(responseLiteral, V2RoomInfoResponse.class);
        when(roomService.v2getInfo(any())).thenReturn(response);

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/guest/api/v2/room/{roomUuid}", "test-room-uuid")
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
                                                .description("Guest용 방 정보 조회 API (V2)")
                                                .tag("Room-Guest")
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                )
        ;
    }
}
