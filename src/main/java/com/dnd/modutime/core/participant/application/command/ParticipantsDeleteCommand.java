package com.dnd.modutime.core.participant.application.command;

import com.dnd.modutime.core.participant.domain.Participant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantsDeleteCommand {

    private String roomUuid;
    private List<Long> participantIds;

    private List<Participant> participants;

    public static ParticipantsDeleteCommand of(String roomUuid, List<Long> participantIds) {
        var command = new ParticipantsDeleteCommand();
        command.roomUuid = roomUuid;
        command.participantIds = participantIds;
        return command;
    }

    public void assign(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Participant> execute() {
        return this.participants;
    }
}
