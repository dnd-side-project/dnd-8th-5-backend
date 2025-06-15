package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.exception.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ParticipantFacade participantFacade;
    private final ParticipantQueryService participantQueryService;

    public AuthService(ParticipantFacade participantFacade, ParticipantQueryService participantQueryService) {
        this.participantFacade = participantFacade;
        this.participantQueryService = participantQueryService;
    }

    public void login(String roomUuid, LoginRequest loginRequest) {
        if (!participantFacade.existsByName(roomUuid, loginRequest.getName())) {
            participantFacade.create(roomUuid, loginRequest.getName(), loginRequest.getPassword());
        }
        Participant participant = participantQueryService.getByRoomUuidAndName(roomUuid, loginRequest.getName());
        if (!participant.matchPassword(loginRequest.getPassword())) {
            throw new InvalidPasswordException();
        }
    }
}
