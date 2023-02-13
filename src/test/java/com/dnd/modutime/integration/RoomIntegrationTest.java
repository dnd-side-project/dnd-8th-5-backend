package com.dnd.modutime.integration;

import com.dnd.modutime.application.RoomService;
import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.domain.Room;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.request.TimerRequest;
import com.dnd.modutime.dto.response.RoomResponse;
import com.dnd.modutime.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static com.dnd.modutime.fixture.RoomFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TimeConfiguration.class)
@SpringBootTest
public class RoomIntegrationTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void 방을_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomResponse roomResponse = roomService.create(roomRequest);
        assertThat(roomResponse.getUuid()).isNotNull();
    }

    @Test
    void 타이머값이_null이면_deaLine은_가지고있지_않는것으로_판단한다() {
        RoomRequest roomRequest = getRoomRequest(null);
        RoomResponse roomResponse = roomService.create(roomRequest);
        Room room = roomRepository.findByUuid(roomResponse.getUuid());
        assertThat(room.hasDeadLine()).isFalse();
    }

    @Test
    void 타이머값이_모두_0으로_들어오면_deadLine은_가지고있지_않는것으로_판단한다() {
        RoomRequest roomRequest = getRoomRequest(new TimerRequest(0, 0, 0));
        RoomResponse roomResponse = roomService.create(roomRequest);
        Room room = roomRepository.findByUuid(roomResponse.getUuid());
        assertThat(room.hasDeadLine()).isFalse();
    }
}
