package com.dnd.modutime.integration;

import static com.dnd.modutime.fixture.RoomFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.application.RoomService;
import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TimeConfiguration.class)
@SpringBootTest
public class RoomIntegrationTest {

    @Autowired
    private RoomService roomService;

    @Test
    void 방을_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        assertThat(roomCreationResponse.getUuid()).isNotNull();
    }
}
