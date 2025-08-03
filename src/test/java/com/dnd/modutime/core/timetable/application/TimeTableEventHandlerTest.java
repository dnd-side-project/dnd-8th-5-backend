package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timeblock.domain.TimeBlockRemovedEvent;
import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.core.timetable.application.command.TimeTableUpdateCommand;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.LocalDateTime;
import java.util.List;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@SpringBootTestWithoutOAuthConfig
@RecordApplicationEvents
class TimeTableEventHandlerTest extends IntegrationSupporter {

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private TimeBlockService timeBlockService;

    @SpyBean
    private TimeTableService timeTableService;

    @MockBean
    private TimeReplaceValidator timeReplaceValidator;

    @DisplayName("타임블록이 삭제되면 타임테이블이 변경이 호출된다.")
    @Test
    void test01() {
        var participantName = "참여자1";
        timeBlockService.create(ROOM_UUID, participantName);
        timeBlockService.remove(ROOM_UUID, participantName);

        // then
        assertAll(
                () -> assertThat(events.stream(TimeBlockRemovedEvent.class).count()).isEqualTo(1),
                () -> verify(timeTableService).update(any(TimeTableUpdateCommand.class))
        );
    }

    @DisplayName("타임블록이 변경되면 타임테이블이 변경이 호출된다.")
    @Test
    void test02() {
        doNothing().when(timeReplaceValidator).validate(any(), any());
        var participantName = "참여자1";
        timeBlockService.create(ROOM_UUID, participantName);
        var request = new TimeReplaceRequest(participantName, true, List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));
        timeBlockService.replace(ROOM_UUID, request);

        // then
        assertAll(
                () -> assertThat(events.stream(TimeBlockReplaceEvent.class).count()).isEqualTo(1),
                () -> verify(timeTableService).update(any(TimeTableUpdateCommand.class))
        );
    }
}
