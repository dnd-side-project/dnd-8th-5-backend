package com.dnd.modutime.controller.room;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.common.ModutimeHostConfigurationProperties;
import com.dnd.modutime.core.room.controller.OpenGraphController;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.dnd.modutime.documentation.DocumentUtils;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class OpenGraphControllerDocsTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AdjustmentResultRepository adjustmentResultRepository;

    private OpenGraphController createController() {
        var hostProperties = new ModutimeHostConfigurationProperties(
                new ModutimeHostConfigurationProperties.Host("https://modutime.site", "https://api.modutime.site")
        );
        return new OpenGraphController(roomRepository, adjustmentResultRepository, hostProperties);
    }

    @DisplayName("Open Graph 미리보기 HTML 조회 - 조율 결과 있음")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "og-invite-room-uuid";
        var controller = createController();

        var pathParameters = new org.springframework.restdocs.request.ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        // Mock Room
        var room = Mockito.mock(Room.class);
        when(room.getTitle()).thenReturn("팀 회의");

        when(roomRepository.findByUuid("test-room-uuid")).thenReturn(Optional.of(room));

        // Mock AdjustmentResult with CandidateDateTime
        var candidateDateTime = Mockito.mock(CandidateDateTime.class);
        when(candidateDateTime.getStartDateTime()).thenReturn(LocalDateTime.of(2025, 7, 16, 13, 0));

        var adjustmentResult = Mockito.mock(AdjustmentResult.class);
        when(adjustmentResult.getCandidateDateTimes()).thenReturn(List.of(candidateDateTime));

        when(adjustmentResultRepository.findByRoomUuid("test-room-uuid")).thenReturn(Optional.of(adjustmentResult));

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(get("/og/invite/{roomUuid}", "test-room-uuid"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                RequestDocumentation.pathParameters(pathParameters)
                        )
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("카카오톡 링크 미리보기용 Open Graph HTML")
                                                .tag("OpenGraph")
                                                .pathParameters(pathParameters)
                                                .build())
                        )
                );
    }

    @DisplayName("Open Graph 미리보기 HTML 조회 - 존재하지 않는 방")
    @Test
    void test02(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "og-invite-room-uuid-not-found";
        var controller = createController();

        when(roomRepository.findByUuid("non-existent-uuid")).thenReturn(Optional.empty());

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(get("/og/invite/{roomUuid}", "non-existent-uuid"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse()
                        )
                );
    }
}
