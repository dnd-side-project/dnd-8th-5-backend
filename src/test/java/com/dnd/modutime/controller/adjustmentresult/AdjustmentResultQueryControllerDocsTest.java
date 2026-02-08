package com.dnd.modutime.controller.adjustmentresult;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponseV1;
import com.dnd.modutime.core.adjustresult.controller.AdjustmentResultController;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.dnd.modutime.documentation.TestJsonUtils;
import com.dnd.modutime.infrastructure.PageResponse;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;

@ApiDocsTest
public class AdjustmentResultQueryControllerDocsTest {

    @Mock
    private AdjustmentResultService adjustmentResultService;

    @InjectMocks
    private AdjustmentResultController controller;

    @DisplayName("조율 결과 우선 순위 목록 조회 API")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "get-api-v1-room-room-uuid-adjustment-result";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID"),
        };

        var queryParameters = new ParameterDescriptor[]{
                RequestDocumentation.parameterWithName("page").description("페이지"),
                RequestDocumentation.parameterWithName("size").description("페이지당 조회크기"),
                RequestDocumentation.parameterWithName("sorted").description("정렬 조건").optional(),
                RequestDocumentation.parameterWithName("participantNames").description("참여자 이름 목록").optional(),
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("pageRequest").type(OBJECT).description("페이징 요청정보"),
                fieldWithPath("pageRequest.page").type(NUMBER).description("페이징 요청 페이지번호"),
                fieldWithPath("pageRequest.size").type(NUMBER).description("페이징 요청 페이징크기(=limit)"),
                fieldWithPath("pageRequest.offset").type(NUMBER).description("페이징 요청 오프셋"),
                fieldWithPath("total").type(NUMBER).description("전체 데이터 수"),
                fieldWithPath("isFirst").type(BOOLEAN).description("첫 번째 페이지 여부"),
                fieldWithPath("isLast").type(BOOLEAN).description("마지막 페이지 여부"),
                fieldWithPath("hasContent").type(BOOLEAN).description("페이징 응답 - 컨텐츠 존재여부"),
                fieldWithPath("hasNext").type(BOOLEAN).description("페이징 응답 - 다음페이지 존재여부"),
                fieldWithPath("hasPrevious").type(BOOLEAN).description("페이징 응답 - 이전페이지 존재여부"),
                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수"),

                fieldWithPath("content").type(ARRAY).description("후보 목록"),
                fieldWithPath("content[].id").type(NUMBER).description("후보 목록 요소: id"),
                fieldWithPath("content[].date").type(STRING).description("후보 목록 요소: 날짜"),
                fieldWithPath("content[].dayOfWeek").type(STRING).description("후보 목록 요소: 요일"),
                fieldWithPath("content[].startTime").type(STRING).description("후보 목록 요소: 시작 일시"),
                fieldWithPath("content[].endTime").type(STRING).description("후보 목록 요소: 종료 일시"),
                fieldWithPath("content[].availableParticipantNames").type(ARRAY).description("후보 목록 요소: 가능한 참여자 이름 목록"),
                fieldWithPath("content[].unavailableParticipantNames").type(ARRAY).description("후보 목록 요소: 불가능한 참여자 이름 목록"),
        };

        //language=JSON
        var responseLiteral = """
                {
                  "pageRequest": {
                    "page": 1,
                    "size": 5,
                    "offset": 0
                  },
                  "total": 1,
                  "hasNext": false,
                  "hasPrevious": false,
                  "totalPages": 1,
                  "hasContent": true,
                  "isFirst": true,
                  "isLast": true,
                  "content": [
                    {
                      "id": 0,
                      "date": "2025-09-30",
                      "dayOfWeek": "일",
                      "startTime": "15:45",
                      "endTime": "15:45",
                      "availableParticipantNames": [
                        "동호",
                        "채민",
                        "주현"
                      ],
                      "unavailableParticipantNames": [
                        "수진",
                        "현"
                      ]
                    }
                  ]
                }
                """;

        Mockito.when(adjustmentResultService.search(any(), any()))
                .thenReturn(TestJsonUtils.readValue(responseLiteral, new TypeReference<PageResponse<CandidateDateTimeResponseV1>>() {
                }));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/api/v1/room/{roomUuid}/adjustment-results", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "1")
                                .param("size", "5")
                                .param("participantNames", "채민", "동호")
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
                                                .description("조율 결과 우선 순위 목록 조회 API")
                                                .tag("adjustment-result")
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .requestParameters(queryParameters)
                                                .build())
                        )
                )
        ;
    }
}
