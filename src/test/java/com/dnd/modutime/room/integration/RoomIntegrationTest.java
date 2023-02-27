package com.dnd.modutime.room.integration;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.room.application.RoomService;
import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.timetable.application.RoomCreationEvent;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@Import(TimeConfiguration.class)
@RecordApplicationEvents
@SpringBootTest
public class RoomIntegrationTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private ApplicationEvents events;

    @Test
    void 방을_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }

    @Test
    void 방이_생성되면_TimeTable이_초기화된다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);

        Optional<TimeTable> actual = timeTableRepository.findByRoomUuid(roomCreationResponse.getUuid());
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(events.stream(RoomCreationEvent.class).count()).isEqualTo(1)
        );
    }
}
