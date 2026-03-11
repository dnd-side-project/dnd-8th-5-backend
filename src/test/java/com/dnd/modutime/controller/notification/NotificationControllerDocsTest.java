package com.dnd.modutime.controller.notification;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.notification.application.NotificationQueryService;
import com.dnd.modutime.core.notification.application.response.NotificationResponse;
import com.dnd.modutime.core.notification.application.response.UnreadCountResponse;
import com.dnd.modutime.core.notification.controller.NotificationController;
import com.dnd.modutime.core.notification.domain.NotificationType;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.infrastructure.PageRequest;
import com.dnd.modutime.infrastructure.PageResponse;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class NotificationControllerDocsTest {

    @Mock
    private NotificationQueryService notificationQueryService;

    @InjectMocks
    private NotificationController controller;

    @DisplayName("알림 목록 조회 API")
    @Test
    void 알림_목록_조회(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "get-api-v1-notifications";

        var notificationResponse = new NotificationResponse(
                1L,
                NotificationType.AVAILABILITY_REGISTERED,
                "가용시간 등록",
                "김철수님이 가용시간을 등록했습니다.",
                Map.of("roomUuid", "room-uuid-123", "participantName", "김철수"),
                false,
                LocalDateTime.of(2026, 3, 10, 14, 30, 0)
        );
        var pageRequest = PageRequest.of(0, 20);
        var pageResponse = PageResponse.of(List.of(notificationResponse), pageRequest, 1L);

        when(notificationQueryService.getNotifications(any(), any(PageRequest.class)))
                .thenReturn(pageResponse);

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("content[]").type(ARRAY).description("알림 목록"),
                fieldWithPath("content[].id").type(NUMBER).description("알림 ID"),
                fieldWithPath("content[].type").type(STRING).description("알림 유형"),
                fieldWithPath("content[].title").type(STRING).description("알림 제목"),
                fieldWithPath("content[].message").type(STRING).description("알림 본문"),
                fieldWithPath("content[].data").type(OBJECT).description("알림 컨텍스트 데이터"),
                fieldWithPath("content[].data.roomUuid").type(STRING).description("방 UUID").optional(),
                fieldWithPath("content[].data.participantName").type(STRING).description("참여자 이름").optional(),
                fieldWithPath("content[].isRead").type(BOOLEAN).description("읽음 여부"),
                fieldWithPath("content[].createdAt").type(ARRAY).description("생성 시각"),
                fieldWithPath("pageRequest.page").type(NUMBER).description("페이지 번호"),
                fieldWithPath("pageRequest.size").type(NUMBER).description("페이지 크기"),
                fieldWithPath("pageRequest.offset").type(NUMBER).description("페이지 오프셋"),
                fieldWithPath("total").type(NUMBER).description("전체 알림 수"),
                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수"),
                fieldWithPath("hasPrevious").type(BOOLEAN).description("이전 페이지 존재 여부"),
                fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지 존재 여부"),
                fieldWithPath("hasContent").type(BOOLEAN).description("컨텐츠 존재 여부"),
                fieldWithPath("isFirst").type(BOOLEAN).description("첫 페이지 여부"),
                fieldWithPath("isLast").type(BOOLEAN).description("마지막 페이지 여부")
        };

        var queryParameters = new org.springframework.restdocs.request.ParameterDescriptor[]{
                parameterWithName("page").description("페이지 번호 (기본값: 0)").optional(),
                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
        };

        var mockMvc = createMockMvc(contextProvider);

        mockMvc.perform(
                        get("/api/v1/notifications")
                                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                requestParameters(queryParameters),
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
                                                .description("알림 목록 조회 API")
                                                .tag("Notification")
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("읽지 않은 알림 수 조회 API")
    @Test
    void 읽지않은_알림_수_조회(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "get-api-v1-notifications-unread-count";

        when(notificationQueryService.getUnreadCount(any()))
                .thenReturn(UnreadCountResponse.of(3L));

        var responseFields = new FieldDescriptor[]{
                fieldWithPath("count").type(NUMBER).description("읽지 않은 알림 수")
        };

        var mockMvc = createMockMvc(contextProvider);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
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
                                                .description("읽지 않은 알림 수 조회 API")
                                                .tag("Notification")
                                                .responseFields(responseFields)
                                                .build())
                        )
                );
    }

    @DisplayName("알림 읽음 처리 API")
    @Test
    void 알림_읽음_처리(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "patch-api-v1-notifications-notification-id-read";

        doNothing().when(notificationQueryService).markAsRead(any(), any());

        var pathParameterDescriptors = new org.springframework.restdocs.request.ParameterDescriptor[]{
                parameterWithName("notificationId").description("알림 ID")
        };

        var mockMvc = createMockMvc(contextProvider);

        mockMvc.perform(patch("/api/v1/notifications/{notificationId}/read", 1L))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                pathParameters(pathParameterDescriptors)
                        )
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("알림 읽음 처리 API")
                                                .tag("Notification")
                                                .pathParameters(pathParameterDescriptors)
                                                .build())
                        )
                );
    }

    @DisplayName("알림 전체 읽음 처리 API")
    @Test
    void 알림_전체_읽음_처리(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "patch-api-v1-notifications-read-all";

        doNothing().when(notificationQueryService).markAllAsRead(any());

        var mockMvc = createMockMvc(contextProvider);

        mockMvc.perform(patch("/api/v1/notifications/read-all"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse()
                        )
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("알림 전체 읽음 처리 API")
                                                .tag("Notification")
                                                .build())
                        )
                );
    }

    private MockMvc createMockMvc(RestDocumentationContextProvider contextProvider) {
        var mockOAuth2User = createMockOAuth2User();
        var documentationConfigurer = documentationConfiguration(contextProvider);
        documentationConfigurer.uris().withScheme("https").withHost(LOCALHOST).withPort(443);

        return MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter,
                                                  ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest,
                                                  WebDataBinderFactory binderFactory) {
                        return mockOAuth2User;
                    }
                })
                .apply(documentationConfigurer)
                .build();
    }

    private OAuth2User createMockOAuth2User() {
        var user = new User("테스트유저", "test@example.com", "profile.jpg", "thumb.jpg", OAuth2Provider.KAKAO);
        return new OAuth2User(user, Collections.singletonMap("id", "12345"), "id");
    }
}
