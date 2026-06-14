package com.dnd.modutime.controller.feedback;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.feedback.application.FeedbackAuthorResolver;
import com.dnd.modutime.core.feedback.application.FeedbackService;
import com.dnd.modutime.core.feedback.application.command.FeedbackAuthor;
import com.dnd.modutime.core.feedback.controller.FeedbackController;
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

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class FeedbackControllerDocsTest {

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private FeedbackAuthorResolver feedbackAuthorResolver;

    @InjectMocks
    private FeedbackController controller;

    @DisplayName("피드백 제출 API")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "post-api-v1-feedback";

        var requestHeaders = new HeaderDescriptor[]{
                headerWithName("Authorization")
                        .description("선택적. 로그인 시 `Bearer {accessToken}` (카카오/비회원 토큰). 비로그인 시 생략")
                        .optional()
        };

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("category").type(STRING).description("피드백 유형 (PRAISE, REVIEW, FEATURE, QUESTION, BUG)"),
                fieldWithPath("content").type(STRING).description("작성 내용 (trim 후 1~1000자)"),
                fieldWithPath("replyEmail").type(STRING).description("회신용 이메일").optional(),
                fieldWithPath("interview").type(OBJECT).description("인터뷰 참여 의향"),
                fieldWithPath("interview.agreed").type(BOOLEAN).description("인터뷰 참여 동의 여부"),
                fieldWithPath("interview.phoneNumber").type(STRING).description("인터뷰 연락처 (agreed=true일 때)").optional(),
                fieldWithPath("responses").type(ARRAY).description("질문-답변 쌍 목록 (최소 1개)"),
                fieldWithPath("responses[].question").type(STRING).description("질문 라벨"),
                fieldWithPath("responses[].answer").type(STRING).description("답변"),
                fieldWithPath("snapshot").type(OBJECT).description("제출 시점 컨텍스트 스냅샷"),
                fieldWithPath("snapshot.user").type(OBJECT).description("로그인 사용자 정보 (비회원이면 null)").optional(),
                fieldWithPath("snapshot.user.name").type(STRING).description("사용자 이름").optional(),
                fieldWithPath("snapshot.user.email").type(STRING).description("사용자 이메일").optional(),
                fieldWithPath("snapshot.room").type(OBJECT).description("방 컨텍스트 (없으면 null)").optional(),
                fieldWithPath("snapshot.room.roomId").type(STRING).description("방 ID").optional(),
                fieldWithPath("snapshot.room.type").type(STRING).description("방 유형 (TABLE, CALENDAR)").optional(),
                fieldWithPath("snapshot.room.title").type(STRING).description("방 제목").optional(),
                fieldWithPath("snapshot.room.participantCount").type(NUMBER).description("참여자 수").optional(),
                fieldWithPath("snapshot.room.headCount").type(NUMBER).description("예상 인원 (없으면 null)").optional(),
                fieldWithPath("snapshot.room.deadLine").type(STRING).description("마감 일시 ISO 문자열 (없으면 null)").optional(),
                fieldWithPath("snapshot.page").type(OBJECT).description("페이지 컨텍스트"),
                fieldWithPath("snapshot.page.routeId").type(STRING).description("라우트 패턴"),
                fieldWithPath("snapshot.page.pathname").type(STRING).description("실제 경로"),
                fieldWithPath("snapshot.page.search").type(STRING).description("쿼리스트링 원문"),
                fieldWithPath("snapshot.env").type(OBJECT).description("실행 환경"),
                fieldWithPath("snapshot.env.userAgent").type(STRING).description("User-Agent"),
                fieldWithPath("snapshot.env.viewport").type(OBJECT).description("뷰포트"),
                fieldWithPath("snapshot.env.viewport.width").type(NUMBER).description("뷰포트 너비"),
                fieldWithPath("snapshot.env.viewport.height").type(NUMBER).description("뷰포트 높이"),
                fieldWithPath("snapshot.env.appVersion").type(STRING).description("앱 버전"),
        };

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("id").type(NUMBER).description("생성된 피드백 ID"),
        };

        //language=JSON
        var requestLiteral = """
                {
                  "category": "BUG",
                  "content": "일정 제출 버튼이 안 눌려요.",
                  "replyEmail": "user@example.com",
                  "interview": { "agreed": true, "phoneNumber": "010-1234-5678" },
                  "responses": [
                    { "question": "사용 중에 문제가 생겼어요", "answer": "일정 제출 버튼이 안 눌려요." }
                  ],
                  "snapshot": {
                    "user": { "name": "김모두", "email": "user@example.com" },
                    "room": {
                      "roomId": "abc123",
                      "type": "TABLE",
                      "title": "팀 회식 날짜",
                      "participantCount": 4,
                      "headCount": 6,
                      "deadLine": "2026-06-20T00:00:00"
                    },
                    "page": { "routeId": "/$roomId/schedules", "pathname": "/abc123/schedules", "search": "" },
                    "env": {
                      "userAgent": "Mozilla/5.0",
                      "viewport": { "width": 390, "height": 844 },
                      "appVersion": "2.0.0"
                    }
                  }
                }
                """;

        when(feedbackAuthorResolver.resolve(any())).thenReturn(FeedbackAuthor.anonymous());
        when(feedbackService.create(any())).thenReturn(1L);

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(
                        post("/api/v1/feedback")
                                .header("Authorization", "Bearer {accessToken}")
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
                                HeaderDocumentation.requestHeaders(requestHeaders),
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
                                                .description("인앱 피드백 제출 API (optional auth)")
                                                .tag("Feedback")
                                                .requestHeaders(requestHeaders)
                                                .requestFields(requestFields)
                                                .responseFields(responseFields)
                                                .build())
                        )
                )
        ;
    }
}
