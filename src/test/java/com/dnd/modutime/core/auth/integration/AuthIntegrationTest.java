package com.dnd.modutime.core.auth.integration;

import com.dnd.modutime.core.auth.application.AuthService;
import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantRepository;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class AuthIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ParticipantQueryService participantQueryService;

    @Test
    void 방에_존재하지_않는_이름과_패스워드로_로그인요청을_하면_새로운_참여자를_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        authService.login(roomCreationResponse.getUuid(), loginRequest);
        Optional<Participant> actual = participantQueryService.getByRoomUuidAndName(
                roomCreationResponse.getUuid(), loginRequest.getName());
        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    void 방에_존재하는_이름과_올바르지_않은_패스워드로_로그인요청을_하면_예외를_반환한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        authService.login(roomCreationResponse.getUuid(), loginRequest);
        assertThatThrownBy(() -> authService.login(roomCreationResponse.getUuid(), new LoginRequest("참여자1", "9999")))
                .isInstanceOf(InvalidPasswordException.class);
    }
}
