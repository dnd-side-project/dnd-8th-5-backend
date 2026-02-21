package com.dnd.modutime.core.adjustresult.application;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.adjustresult.application.command.AdjustmentResultReplaceCommand;
import com.dnd.modutime.core.timetable.application.TimeTableService;
import com.dnd.modutime.core.timetable.application.command.TimeTableUpdateCommand;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.util.IntegrationSupporter;
import com.dnd.modutime.util.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTestWithoutOAuthConfig
class AdjustmentResultEventHandlerTest extends IntegrationSupporter {

    @Autowired
    private TimeTableService timeTableService;

    @MockBean
    private TimeTableRepository timeTableRepository;

    @MockBean
    private AdjustmentResultReplaceService adjustmentResultReplaceService;

    @DisplayName("타임테이블이 변경되면 조율결과 변경이 호출된다.")
    @Test
    void test01() {
        //language=json
        var timeTableLiteral = """
                {
                  "roomUuid": "room-uuid"
                }
                """;
        var timeTable = JsonUtils.readValue(timeTableLiteral, TimeTable.class);
        given(timeTableRepository.findByRoomUuid(any())).willReturn(Optional.of(timeTable));
        timeTableService.update(TimeTableUpdateCommand.of(
                "room-uuid",
                List.of(),
                List.of(),
                "참여자1"
        ));

        verify(adjustmentResultReplaceService).replace(any(AdjustmentResultReplaceCommand.class));
    }
}