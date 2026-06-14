package com.dnd.modutime.controller.admin;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.admin.feedback.application.AdminFeedbackService;
import com.dnd.modutime.core.admin.feedback.application.response.FeedbackDetailResponse;
import com.dnd.modutime.core.admin.feedback.application.response.FeedbackRowResponse;
import com.dnd.modutime.core.admin.feedback.controller.AdminFeedbackController;
import com.dnd.modutime.core.feedback.domain.FeedbackCategory;
import com.dnd.modutime.core.feedback.domain.ResponsePair;
import com.dnd.modutime.core.feedback.domain.Snapshot;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static com.dnd.modutime.documentation.MockMvcFactory.HEADER_AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class AdminFeedbackControllerDocsTest {

    @Mock
    private AdminFeedbackService adminFeedbackService;

    @InjectMocks
    private AdminFeedbackController controller;

    private static final HeaderDescriptor[] AUTH_HEADERS = new HeaderDescriptor[]{
            headerWithName("Authorization").description("어드민 access token (Bearer {accessToken})")
    };

    private FeedbackRowResponse sampleRow() {
        return new FeedbackRowResponse(
                1L,
                FeedbackCategory.BUG,
                "일정 제출 버튼이 안 눌려요",
                "user@example.com",
                true,
                "010-1234-5678",
                List.of(new ResponsePair("사용 중에 문제가 생겼어요", "일정 제출 버튼이 안 눌려요")),
                "high",
                "open",
                LocalDateTime.of(2026, 6, 13, 9, 24),
                LocalDateTime.of(2026, 6, 13, 9, 24)
        );
    }

    private FeedbackDetailResponse sampleDetail() {
        return new FeedbackDetailResponse(
                1L,
                FeedbackCategory.BUG,
                "일정 제출 버튼이 안 눌려요",
                "user@example.com",
                true,
                "010-1234-5678",
                List.of(new ResponsePair("사용 중에 문제가 생겼어요", "일정 제출 버튼이 안 눌려요")),
                "high",
                "open",
                LocalDateTime.of(2026, 6, 13, 9, 24),
                LocalDateTime.of(2026, 6, 13, 9, 24),
                new Snapshot(
                        new Snapshot.User("김모두", "user@example.com"),
                        new Snapshot.Room("abc123", "TABLE", "팀 회식 날짜", 4, 6, "2026-06-20T00:00:00"),
                        new Snapshot.Page("/$roomId/schedules", "/abc123/schedules", ""),
                        new Snapshot.Env("Mozilla/5.0", new Snapshot.Viewport(390, 844), "2.0.0")
                )
        );
    }

    /**
     * 목록 row 필드 (snapshot 제외). prefix 를 붙여 목록(`[].`)/상세(``) 양쪽에 재사용한다.
     */
    private FieldDescriptor[] rowFields(String prefix) {
        return new FieldDescriptor[]{
                fieldWithPath(prefix + "id").type(NUMBER).description("피드백 ID"),
                fieldWithPath(prefix + "category").type(STRING)
                        .description("제출 분류 (PRAISE | REVIEW | FEATURE | QUESTION | BUG)"),
                fieldWithPath(prefix + "content").type(STRING).description("작성 내용"),
                fieldWithPath(prefix + "reply_email").type(STRING).optional().description("회신용 이메일 (nullable)"),
                fieldWithPath(prefix + "interview_agreed").type(BOOLEAN).description("인터뷰 참여 동의 여부"),
                fieldWithPath(prefix + "interview_phone_number").type(STRING).optional()
                        .description("인터뷰 연락처 (nullable)"),
                fieldWithPath(prefix + "responses").type(ARRAY).description("질문-답변 쌍 목록"),
                fieldWithPath(prefix + "responses[].question").type(STRING).description("질문 라벨"),
                fieldWithPath(prefix + "responses[].answer").type(STRING).optional().description("답변"),
                fieldWithPath(prefix + "severity").type(STRING)
                        .description("심각도 (low | medium | high | critical, 어드민 트리아지)"),
                fieldWithPath(prefix + "status").type(STRING)
                        .description("처리 상태 (open | in_progress | resolved | closed, 어드민 트리아지)"),
                fieldWithPath(prefix + "created_at").type(STRING).description("제출 시각 (ISO 8601)"),
                fieldWithPath(prefix + "updated_at").type(STRING).description("최종 변경 시각 (ISO 8601)")
        };
    }

    private FieldDescriptor[] snapshotFields() {
        return new FieldDescriptor[]{
                fieldWithPath("snapshot").type(OBJECT).optional().description("제출 시점 컨텍스트 (미수집 시 null, 내부 키 camelCase)"),
                fieldWithPath("snapshot.user").type(OBJECT).optional().description("작성자 정보 (비로그인 시 null)"),
                fieldWithPath("snapshot.user.name").type(STRING).optional().description("작성자 이름"),
                fieldWithPath("snapshot.user.email").type(STRING).optional().description("작성자 이메일"),
                fieldWithPath("snapshot.room").type(OBJECT).optional().description("방 컨텍스트 (없을 시 null)"),
                fieldWithPath("snapshot.room.roomId").type(STRING).optional().description("방 ID"),
                fieldWithPath("snapshot.room.type").type(STRING).optional().description("방 타입 (TABLE | CALENDAR)"),
                fieldWithPath("snapshot.room.title").type(STRING).optional().description("방 제목"),
                fieldWithPath("snapshot.room.participantCount").type(NUMBER).optional().description("참여자 수"),
                fieldWithPath("snapshot.room.headCount").type(NUMBER).optional().description("모집 인원 (nullable)"),
                fieldWithPath("snapshot.room.deadLine").type(STRING).optional().description("마감 일시 (ISO 8601, nullable)"),
                fieldWithPath("snapshot.page").type(OBJECT).description("페이지 컨텍스트"),
                fieldWithPath("snapshot.page.routeId").type(STRING).description("라우트 ID"),
                fieldWithPath("snapshot.page.pathname").type(STRING).description("경로"),
                fieldWithPath("snapshot.page.search").type(STRING).description("쿼리스트링"),
                fieldWithPath("snapshot.env").type(OBJECT).description("환경 정보"),
                fieldWithPath("snapshot.env.userAgent").type(STRING).description("User-Agent"),
                fieldWithPath("snapshot.env.viewport").type(OBJECT).description("뷰포트"),
                fieldWithPath("snapshot.env.viewport.width").type(NUMBER).description("뷰포트 너비"),
                fieldWithPath("snapshot.env.viewport.height").type(NUMBER).description("뷰포트 높이"),
                fieldWithPath("snapshot.env.appVersion").type(STRING).description("앱 버전")
        };
    }

    @DisplayName("어드민 피드백 목록 조회 - snapshot 제외, 제출 최신순")
    @Test
    void 목록_조회(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-get-feedback-list";
        var responseFields = prefixedArrayFields(rowFields("[]."));

        when(adminFeedbackService.getList()).thenReturn(List.of(sampleRow()));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/admin/api/v1/feedback")
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
                                                .description("어드민 피드백 목록 조회 (snapshot 제외)")
                                                .tag("Admin-Feedback")
                                                .requestHeaders(AUTH_HEADERS)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("어드민 피드백 상세 조회 - snapshot 포함")
    @Test
    void 상세_조회(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-get-feedback-detail";
        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("id").description("피드백 ID")
        };
        var responseFields = concat(rowFields(""), snapshotFields());

        when(adminFeedbackService.getDetail(eq(1L))).thenReturn(sampleDetail());

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        get("/admin/api/v1/feedback/{id}", 1L)
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
                                                .description("어드민 피드백 상세 조회 (snapshot 포함)")
                                                .tag("Admin-Feedback")
                                                .requestHeaders(AUTH_HEADERS)
                                                .pathParameters(pathParameters)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("어드민 피드백 트리아지 - 심각도/상태 부분 수정")
    @Test
    void 트리아지(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "admin-patch-feedback";
        var pathParameters = new ParameterDescriptor[]{
                parameterWithName("id").description("피드백 ID")
        };
        var requestFields = new FieldDescriptor[]{
                fieldWithPath("severity").type(STRING).optional()
                        .description("심각도 (low | medium | high | critical), 선택"),
                fieldWithPath("status").type(STRING).optional()
                        .description("처리 상태 (open | in_progress | resolved | closed), 선택")
        };
        var responseFields = rowFields("");

        when(adminFeedbackService.triage(eq(1L), any())).thenReturn(sampleRow());

        //language=JSON
        var requestBody = """
                {
                  "severity": "high",
                  "status": "in_progress"
                }
                """;

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        patch("/admin/api/v1/feedback/{id}", 1L)
                                .header("Authorization", HEADER_AUTHORIZATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                HeaderDocumentation.requestHeaders(AUTH_HEADERS),
                                RequestDocumentation.pathParameters(pathParameters),
                                PayloadDocumentation.requestFields(requestFields),
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
                                                .description("어드민 피드백 트리아지 (심각도/상태 부분 수정)")
                                                .tag("Admin-Feedback")
                                                .requestHeaders(AUTH_HEADERS)
                                                .pathParameters(pathParameters)
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    private FieldDescriptor[] concat(FieldDescriptor[] a, FieldDescriptor[] b) {
        var result = new FieldDescriptor[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * 배열 응답의 루트({@code []})를 명시적으로 문서화에 포함시킨다.
     */
    private FieldDescriptor[] prefixedArrayFields(FieldDescriptor[] elementFields) {
        var root = new FieldDescriptor[]{
                fieldWithPath("[]").type(ARRAY).description("피드백 목록")
        };
        return concat(root, elementFields);
    }
}
