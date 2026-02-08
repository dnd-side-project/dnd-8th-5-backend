package com.dnd.modutime.controller.timetable;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.timetable.application.TimeTableFacade;
import com.dnd.modutime.core.timetable.controller.TimeTableGuestQueryController;
import com.dnd.modutime.core.timetable.domain.view.TimeTableOverview;
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
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class TimeTableGuestQueryControllerDocsTest {

    @Mock
    private TimeTableFacade facade;

    @InjectMocks
    private TimeTableGuestQueryController controller;

    @DisplayName("Guest 실시간 참여 현황 조회 API")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-get-api-room-room-uuid-available-time-overview";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var queryParameters = new ParameterDescriptor[]{
                RequestDocumentation.parameterWithName("participantNames").description("참여자 이름 목록").optional()
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("availableDateTimes").type(ARRAY).description("가능한 날짜/시간 목록"),
                fieldWithPath("availableDateTimes[].availableDate").type(STRING).description("가능한 날짜"),
                fieldWithPath("availableDateTimes[].availableTimeInfos").type(ARRAY).description("가능한 날짜: 시간 목록"),
                fieldWithPath("availableDateTimes[].availableTimeInfos[].timeInfoId").type(NUMBER).description("시간 목록: 시간 정보 ID"),
                fieldWithPath("availableDateTimes[].availableTimeInfos[].time").type(STRING).description("시간 목록: 시간"),
                fieldWithPath("availableDateTimes[].availableTimeInfos[].count").type(NUMBER).description("시간 목록: 참여자 수"),
        };

        //language=JSON
        var responseLiteral = """
                {
                  "availableDateTimes": [
                    {
                      "availableDate": "2025-07-16",
                      "availableTimeInfos": [
                        {
                          "timeInfoId": 3273651,
                          "time": "13:00",
                          "count": 1
                        },
                        {
                          "timeInfoId": 3273652,
                          "time": "13:30",
                          "count": 1
                        }
                      ]
                    }
                  ]
                }
                """;
        var response = TestJsonUtils.readValue(responseLiteral, TimeTableOverview.class);
        when(facade.getOverview(any())).thenReturn(response);

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/guest/api/room/{roomUuid}/available-time/overview", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("participantNames", "채민", "동호")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo( //Spring REST Docs
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.pathParameters(pathParameters),
                                RequestDocumentation.requestParameters(queryParameters),
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
                                                .description("Guest용 참여자별 실시간 참여 현황 조회 API")
                                                .tag("TimeTable-Guest")
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .requestParameters(queryParameters)
                                                .build())
                        )
                )
        ;
    }
}
