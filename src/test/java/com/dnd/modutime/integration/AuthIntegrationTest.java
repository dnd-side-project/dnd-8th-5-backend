package com.dnd.modutime.integration;

import static com.dnd.modutime.fixture.RoomFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dnd.modutime.auth.AuthService;
import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.application.RoomService;
import com.dnd.modutime.domain.Participant;
import com.dnd.modutime.dto.request.LoginRequest;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.response.RoomResponse;
import com.dnd.modutime.repository.ParticipantRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    void 방에_존재하지_않는_이름과_패스워드로_로그인요청을_하면_새로운_참여자를_생성한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomResponse roomResponse = roomService.create(roomRequest);
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        authService.login(roomResponse.getUuid(), loginRequest);
        Optional<Participant> actual = participantRepository.findByRoomUuidAndName(
                roomResponse.getUuid(), loginRequest.getName());
        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    void 방에_존재하는_이름과_올바르지_않은_패스워드로_로그인요청을_하면_예외를_반환한다() {
        RoomRequest roomRequest = getRoomRequest();
        RoomResponse roomResponse = roomService.create(roomRequest);
        LoginRequest loginRequest = new LoginRequest("참여자1", "1234");
        authService.login(roomResponse.getUuid(), loginRequest);
        assertThatThrownBy(() -> authService.login(roomResponse.getUuid(), new LoginRequest("참여자1", "9999")))
                .isInstanceOf(InvalidPasswordException.class);
    }
}
