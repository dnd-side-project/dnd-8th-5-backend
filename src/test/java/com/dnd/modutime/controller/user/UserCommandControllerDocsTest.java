package com.dnd.modutime.controller.user;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.application.UserWithdrawFacade;
import com.dnd.modutime.core.user.controller.UserCommandController;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static com.dnd.modutime.documentation.MockMvcFactory.HEADER_AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class UserCommandControllerDocsTest {

    private final UserWithdrawFacade userWithdrawFacade = Mockito.mock(UserWithdrawFacade.class);
    private final UserCommandController controller = new UserCommandController(userWithdrawFacade);

    @DisplayName("회원 탈퇴 API")
    @Test
    void 회원_탈퇴(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "delete-api-v1-users-me";

        var requestHeaders = new HeaderDescriptor[]{
                headerWithName("Authorization").description("인증 토큰 (Bearer {JWT-TOKEN})")
        };

        var requestFields = new FieldDescriptor[]{
                fieldWithPath("reason").type(STRING).description("탈퇴 사유 (화면 라디오 라벨 또는 자유서술, 최대 200자)"),
                fieldWithPath("agreed").type(BOOLEAN).description("데이터 영구 삭제 동의 여부 (true 만 허용)"),
        };

        //language=JSON
        var requestLiteral = """
                {
                  "reason": "기타: 알림이 너무 자주 와서 부담스러웠어요.",
                  "agreed": true
                }
                """;

        var mockOAuth2User = createMockOAuth2User();

        var argumentResolvers = new HandlerMethodArgumentResolver[]{
                new HandlerMethodArgumentResolver() {
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
                }
        };

        var mockMvc = MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, argumentResolvers, controller);

        mockMvc.perform(
                        delete("/api/v1/users/me")
                                .header("Authorization", HEADER_AUTHORIZATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestLiteral)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                HeaderDocumentation.requestHeaders(requestHeaders),
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
                                                .description("회원 탈퇴 API. 탈퇴 사유와 데이터 영구 삭제 동의를 함께 받고, 카카오 연결을 해제한 뒤 사용자 정보를 익명화합니다.")
                                                .tag("User")
                                                .requestHeaders(requestHeaders)
                                                .requestFields(requestFields)
                                                .build())
                        )
                );
    }

    private OAuth2User createMockOAuth2User() {
        var user = new User("테스트유저", "test@example.com", "profile.jpg", "thumb.jpg", OAuth2Provider.KAKAO, "12345");
        return new OAuth2User(user, Collections.singletonMap("id", "12345"), "id");
    }
}
