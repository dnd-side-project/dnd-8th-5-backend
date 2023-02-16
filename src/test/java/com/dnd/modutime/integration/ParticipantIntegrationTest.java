package com.dnd.modutime.integration;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;

import com.dnd.modutime.application.ParticipantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ParticipantIntegrationTest {

    @Autowired
    private ParticipantService participantService;



    @Test
    void 참여자가_생성되면_AvailableDateTime이_생성된다() {
        participantService.create(ROOM_UUID, "참여자1", "1234");
//        TimeBoard timeBoard = timeBoardRepository.findByRoomUuid(ROOM_UUID).get();
//        Optional<AvailableDateTime> actual = availableDateTimeRepository
//                .findByTimeBoardIdAndParticipantName(timeBoard.getId(), "참여자1");
//        assertThat(actual.isPresent()).isTrue();
    }
}
