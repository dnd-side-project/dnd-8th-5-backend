package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ParticipantFacadeTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ParticipantFacade facade;

    @Autowired
    private ParticipantQueryService queryService;

    @Test
    void 방에_존재하지_않는_이름과_패스워드로_로그인요청을_하면_새로운_참여자를_생성한다() {
        // given
        var roomRequest = getRoomRequest();
        var roomCreationResponse = roomService.create(roomRequest);

        // when
        var command = ParticipantCreateCommand.of(roomCreationResponse.getUuid(), "참여자1", "1234");
        facade.login(command);

        // then
        var actual = queryService.getByRoomUuidAndName(
                roomCreationResponse.getUuid(), command.getName());
        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    void 방에_존재하는_이름과_올바르지_않은_패스워드로_로그인요청을_하면_예외를_반환한다() {
        // given
        var roomRequest = getRoomRequest();
        var roomCreationResponse = roomService.create(roomRequest);
        facade.login(ParticipantCreateCommand.of(roomCreationResponse.getUuid(), "참여자1", "1234"));

        // when & then
        var command = ParticipantCreateCommand.of(roomCreationResponse.getUuid(), "참여자1", "9999");
        assertThatThrownBy(() -> facade.login(command))
                .isInstanceOf(InvalidPasswordException.class);

    }
}
