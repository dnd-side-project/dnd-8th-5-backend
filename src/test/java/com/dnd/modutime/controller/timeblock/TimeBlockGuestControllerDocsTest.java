package com.dnd.modutime.controller.timeblock;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import com.dnd.modutime.core.timeblock.controller.TimeBlockGuestController;
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
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class TimeBlockGuestControllerDocsTest {

    @Mock
    private TimeBlockService timeBlockService;

    @InjectMocks
    private TimeBlockGuestController controller;

    @DisplayName("Guest 참여자 시간 조회 API")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "guest-get-api-room-room-uuid-available-time";

        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        var queryParameters = new ParameterDescriptor[]{
                RequestDocumentation.parameterWithName("name").description("참여자 이름")
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("name").type(STRING).description("참여자 이름"),
                fieldWithPath("availableDateTimes").type(ARRAY).description("등록한 날짜/시간 목록 (형식: yyyy-MM-dd HH:mm)"),
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

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/guest/api/room/{roomUuid}/available-time", "test-room-uuid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("name", "동호")
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
                                                .description("Guest용 참여자 시간 조회 API")
                                                .tag("TimeBlock-Guest")
                                                .responseFields(responseFields)
                                                .pathParameters(pathParameters)
                                                .requestParameters(queryParameters)
                                                .build())
                        )
                )
        ;
    }
}
