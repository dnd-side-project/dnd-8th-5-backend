package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantJoinCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.exception.InvalidPasswordException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipantFacade {

    private final ParticipantQueryService queryService;
    private final ParticipantCommandHandler commandHandler;

    public ParticipantFacade(ParticipantQueryService queryService,
                             ParticipantCommandHandler commandHandler) {
        this.queryService = queryService;
        this.commandHandler = commandHandler;
    }

    public void login(ParticipantCreateCommand command) {
        if (!queryService.existsBy(command.getRoomUuid(), command.getName())) {
            commandHandler.handle(command);
            return;
        }
        var participant = queryService.findByRoomUuidAndName(command.getRoomUuid(), command.getName());
        if (!participant.matchPassword(command.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

    @Transactional
    public void delete(ParticipantsDeleteCommand command) {
        var participants = queryService.getByRoomUuidAndIds(command.getRoomUuid(), command.getParticipantIds());
        command.assign(participants);
        commandHandler.handle(command);
    }

    @Transactional
    public void joinAsOAuthUser(ParticipantJoinCommand command) {
        if (queryService.existsByRoomUuidAndUserId(command.getRoomUuid(), command.getUserId())) {
            throw new IllegalArgumentException("이미 해당 방에 참여한 사용자입니다.");
        }
        if (queryService.existsBy(command.getRoomUuid(), command.getName())) {
            throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
        }
        commandHandler.handle(command);
    }
}
