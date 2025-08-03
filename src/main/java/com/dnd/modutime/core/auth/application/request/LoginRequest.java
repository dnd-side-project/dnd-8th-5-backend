package com.dnd.modutime.core.auth.application.request;

import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequest {

    private String name;
    private String password;

    public ParticipantCreateCommand toParticipantCreateCommand(String roomUuid) {
        return ParticipantCreateCommand.of(roomUuid, this.name, this.password);
    }
}
