package com.dnd.modutime.controller.admin;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.admin.metrics.application.AdminMetricsService;
import com.dnd.modutime.core.admin.metrics.application.response.ServiceMetrics;
import com.dnd.modutime.core.admin.metrics.application.response.ServiceTrends;
import com.dnd.modutime.core.admin.metrics.application.response.TrendPoint;
import com.dnd.modutime.core.admin.metrics.controller.AdminMetricsController;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.util.List;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static com.dnd.modutime.documentation.MockMvcFactory.HEADER_AUTHORIZATION;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class AdminMetricsControllerDocsTest {

    @Mock
    private AdminMetricsService adminMetricsService;

    @InjectMocks
    private AdminMetricsController controller;

    private static final HeaderDescriptor[] AUTH_HEADERS = new HeaderDescriptor[]{
            headerWithName("Authorization").description("어드민 access token (Bearer {accessToken})")
    };

    @DisplayName("어드민 대시보드 요약 지표 조회")
    @Test
    void 요약_지표_조회(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-get-metrics";
        var responseFields = new FieldDescriptor[]{
                fieldWithPath("totalUsers").type(NUMBER).description("전체 사용자 수 (로그인 + 비로그인)"),
                fieldWithPath("loggedInUsers").type(NUMBER).description("로그인(회원) 사용자 수"),
                fieldWithPath("anonymousUsers").type(NUMBER).description("비로그인(게스트) 사용자 수"),
                fieldWithPath("totalRooms").type(NUMBER).description("누적 방 갯수"),
                fieldWithPath("activeRooms").type(NUMBER).description("최근 7일 활성 방 갯수 (최근 7일 내 참여 활동)"),
                fieldWithPath("totalParticipants").type(NUMBER).description("누적 참여자 수"),
                fieldWithPath("newRoomsLast7d").type(NUMBER).description("최근 7일 신규 방 갯수")
        };

        when(adminMetricsService.getMetrics())
                .thenReturn(new ServiceMetrics(4820, 1735, 3085, 9240, 412, 38150, 286));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/admin/api/v1/metrics")
                                .header("Authorization", HEADER_AUTHORIZATION)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                HeaderDocumentation.requestHeaders(AUTH_HEADERS),
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
                                                .description("어드민 대시보드 요약 지표 조회 (현재 시점 스냅샷)")
                                                .tag("Admin-Metrics")
                                                .requestHeaders(AUTH_HEADERS)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("어드민 대시보드 추이 조회 - 일별 30 / 월별 12")
    @Test
    void 추이_조회(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-get-metrics-trends";
        var responseFields = new FieldDescriptor[]{
                fieldWithPath("daily").type(ARRAY).description("최근 30일 일별 추이 (정확히 30개, 오래된→최신)"),
                fieldWithPath("daily[].label").type(STRING).description("x축 라벨 (\"M/D\")"),
                fieldWithPath("daily[].rooms").type(NUMBER).description("해당 일 신규 방 갯수"),
                fieldWithPath("daily[].loggedIn").type(NUMBER).description("해당 일 신규 로그인 사용자"),
                fieldWithPath("daily[].anonymous").type(NUMBER).description("해당 일 신규 비로그인 사용자"),
                fieldWithPath("daily[].participants").type(NUMBER).description("해당 일 참여자 수"),
                fieldWithPath("monthly").type(ARRAY).description("최근 12개월 월별 추이 (정확히 12개, 오래된→최신)"),
                fieldWithPath("monthly[].label").type(STRING).description("x축 라벨 (\"M월\")"),
                fieldWithPath("monthly[].rooms").type(NUMBER).description("해당 월 신규 방 갯수"),
                fieldWithPath("monthly[].loggedIn").type(NUMBER).description("해당 월 신규 로그인 사용자"),
                fieldWithPath("monthly[].anonymous").type(NUMBER).description("해당 월 신규 비로그인 사용자"),
                fieldWithPath("monthly[].participants").type(NUMBER).description("해당 월 참여자 수")
        };

        when(adminMetricsService.getTrends())
                .thenReturn(new ServiceTrends(
                        List.of(new TrendPoint("6/13", 18, 6, 11, 70),
                                new TrendPoint("6/14", 24, 8, 14, 98)),
                        List.of(new TrendPoint("5월", 520, 180, 320, 2100),
                                new TrendPoint("6월", 286, 96, 175, 1240))
                ));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/admin/api/v1/metrics/trends")
                                .header("Authorization", HEADER_AUTHORIZATION)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                HeaderDocumentation.requestHeaders(AUTH_HEADERS),
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
                                                .description("어드민 대시보드 추이 조회 (일별 30 / 월별 12)")
                                                .tag("Admin-Metrics")
                                                .requestHeaders(AUTH_HEADERS)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }
}
