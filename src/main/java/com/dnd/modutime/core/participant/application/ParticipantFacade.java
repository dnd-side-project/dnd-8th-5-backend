package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.participant.domain.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParticipantFacade {

    private final ParticipantQueryService queryService;
    private final ParticipantCommandHandler commandHandler;

    public ParticipantFacade(ParticipantQueryService queryService, ParticipantCommandHandler commandHandler) {
        this.queryService = queryService;
        this.commandHandler = commandHandler;
    }

    public void create(ParticipantCreateCommand command) {
        commandHandler.handle(command);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String roomUuid, String name) {
        return queryService.existsBy(roomUuid, name);
    }

    public void registerEmail(String roomUuid,
                              EmailCreationRequest emailCreationRequest) {
        var participant = queryService.findByRoomUuidAndName(roomUuid, emailCreationRequest.getName());
        participant.registerEmail(new Email(emailCreationRequest.getEmail()));
    }

    public EmailResponse getEmail(String roomUuid, String name) {
        var participant = queryService.findByRoomUuidAndName(roomUuid, name);
        return EmailResponse.from(participant.getEmailOrNull());
    }

    public void delete(ParticipantsDeleteCommand command) {
        var participants = queryService.getByRoomUuidAndName(command.getRoomUuid(), command.getParticipantNames());
        command.assign(participants);
        commandHandler.handle(command);
    }
}
