package com.dnd.modutime.controller.room;

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.core.common.ModutimeHostConfigurationProperties;
import com.dnd.modutime.core.room.controller.OpenGraphController;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
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

import java.time.LocalDate;
import java.time.LocalTime;
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

    private OpenGraphController createController() {
        var hostProperties = new ModutimeHostConfigurationProperties(
                new ModutimeHostConfigurationProperties.Host("https://modutime.site", "https://api.modutime.site")
        );
        return new OpenGraphController(roomRepository, hostProperties);
    }

    @DisplayName("Open Graph 미리보기 HTML 조회 - 날짜+시간 모드")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "og-invite-room-uuid";
        var controller = createController();

        var pathParameters = new org.springframework.restdocs.request.ParameterDescriptor[]{
                parameterWithName("roomUuid").description("방 UUID")
        };

        // Mock Room with date+time
        var room = Mockito.mock(Room.class);
        when(room.getTitle()).thenReturn("팀 회의");
        when(room.getRoomDates()).thenReturn(List.of(new RoomDate(LocalDate.of(2025, 7, 16))));
        when(room.hasStartAndEndTime()).thenReturn(true);
        when(room.getStartTimeOrNull()).thenReturn(LocalTime.of(13, 0));
        when(room.getEndTimeOrNull()).thenReturn(LocalTime.of(15, 0));

        when(roomRepository.findByUuid("test-room-uuid")).thenReturn(Optional.of(room));

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
