package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantJoinCommand;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.core.user.OAuth2Provider;
import com.dnd.modutime.core.user.User;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTestWithoutOAuthConfig
class ParticipantFacadeTest extends IntegrationSupporter {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ParticipantFacade facade;

    @Autowired
    private ParticipantQueryService queryService;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void OAuth_사용자가_방에_참여한다() {
        // given
        var roomUuid = roomService.create(getRoomRequest()).getUuid();
        var user = userRepository.save(new User("테스트", "join-test@example.com", "profile", "thumb", OAuth2Provider.KAKAO));

        // when
        facade.joinAsOAuthUser(ParticipantJoinCommand.of(roomUuid, "테스트", user.getId()));

        // then
        var participant = queryService.findByRoomUuidAndUserId(roomUuid, user.getId());
        assertThat(participant).isPresent();
    }

    @Test
    void 이미_참여한_OAuth_사용자가_다시_참여하면_예외를_반환한다() {
        // given
        var roomUuid = roomService.create(getRoomRequest()).getUuid();
        var user = userRepository.save(new User("테스트", "duplicate-test@example.com", "profile", "thumb", OAuth2Provider.KAKAO));
        facade.joinAsOAuthUser(ParticipantJoinCommand.of(roomUuid, "테스트", user.getId()));

        // when & then
        var command = ParticipantJoinCommand.of(roomUuid, "다른이름", user.getId());
        assertThatThrownBy(() -> facade.joinAsOAuthUser(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 방에 참여한 사용자입니다.");
    }

    @Test
    void 비로그인_참여자가_있는_방에_OAuth_사용자가_참여할_수_있다() {
        // given
        var roomUuid = roomService.create(getRoomRequest()).getUuid();
        facade.login(ParticipantCreateCommand.of(roomUuid, "게스트", "1234"));

        var user = userRepository.save(new User("OAuth유저", "oauth@example.com", "profile", "thumb", OAuth2Provider.KAKAO));

        // when
        facade.joinAsOAuthUser(ParticipantJoinCommand.of(roomUuid, "OAuth유저", user.getId()));

        // then
        var participant = queryService.findByRoomUuidAndUserId(roomUuid, user.getId());
        assertThat(participant).isPresent();
    }
}
