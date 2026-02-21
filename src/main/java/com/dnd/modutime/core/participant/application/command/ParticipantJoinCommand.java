package com.dnd.modutime.core.participant.application.command;

import com.dnd.modutime.core.participant.domain.Participant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantJoinCommand {
    private String roomUuid;
    private String name;
    private Long userId;

    public static ParticipantJoinCommand of(String roomUuid, String name, Long userId) {
        var command = new ParticipantJoinCommand();
        command.roomUuid = roomUuid;
        command.name = name;
        command.userId = userId;
        return command;
    }

    public Participant execute() {
        return Participant.of(this.roomUuid, this.name, this.userId);
    }
}
