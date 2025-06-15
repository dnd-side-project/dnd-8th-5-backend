package com.dnd.modutime.core.participant.application.command;

import com.dnd.modutime.core.participant.domain.Participant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantCreateCommand {
    private String roomUuid;
    private String name;
    private String password;

    public static ParticipantCreateCommand of(String roomUuid, String name, String password) {
        var command = new ParticipantCreateCommand();
        command.roomUuid = roomUuid;
        command.name = name;
        command.password = password;
        return command;
    }

    public Participant execute() {
        return new Participant(
                this.roomUuid,
                this.name,
                this.password);
    }
}
