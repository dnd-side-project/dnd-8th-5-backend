package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.core.participant.application.ParticipantService;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.auth.application.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ParticipantService participantService;

    public void login(String roomUuid, LoginRequest loginRequest) {
        if (!participantService.existsByName(roomUuid, loginRequest.getName())) {
            participantService.create(roomUuid, loginRequest.getName(), loginRequest.getPassword());
        }
        Participant participant = participantService.getByRoomUuidAndName(roomUuid, loginRequest.getName());
        if (!participant.matchPassword(loginRequest.getPassword())) {
            throw new InvalidPasswordException();
        }
    }
}
