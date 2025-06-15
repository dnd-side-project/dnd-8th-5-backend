package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.participant.domain.Email;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParticipantFacade {

    private final ParticipantRepository participantRepository;
    private final ParticipantQueryService participantQueryService;
    private final ParticipantCommandHandler participantCommandHandler;

    public ParticipantFacade(ParticipantRepository participantRepository,
                             ParticipantQueryService participantQueryService, ParticipantCommandHandler participantCommandHandler) {
        this.participantRepository = participantRepository;
        this.participantQueryService = participantQueryService;
        this.participantCommandHandler = participantCommandHandler;
    }

    public void create(String roomUuid, String name, String password) {
        var participant = new Participant(roomUuid, name, password);
        participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String roomUuid, String name) {
        return participantQueryService.existsBy(roomUuid, name);
    }

    public void registerEmail(String roomUuid,
                              EmailCreationRequest emailCreationRequest) {
        var participant = participantQueryService.getByRoomUuidAndName(roomUuid, emailCreationRequest.getName());
        participant.registerEmail(new Email(emailCreationRequest.getEmail()));
    }

    public EmailResponse getEmail(String roomUuid, String name) {
        var participant = participantQueryService.getByRoomUuidAndName(roomUuid, name);
        return EmailResponse.from(participant.getEmailOrNull());
    }

    public void delete(ParticipantsDeleteCommand command) {
        var participants = participantQueryService.getByRoomUuidAndName(command.getRoomUuid(), command.getParticipantNames());
        command.assign(participants);
        participantCommandHandler.handle(command);
    }
}
