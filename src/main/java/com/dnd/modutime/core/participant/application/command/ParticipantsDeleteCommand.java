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
    private List<String> participantNames;

    private List<Participant> participants;

    public static ParticipantsDeleteCommand of(String roomUuid, List<String> participantNames) {
        var command = new ParticipantsDeleteCommand();
        command.roomUuid = roomUuid;
        command.participantNames = participantNames;
        return command;
    }

    public void assign(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Participant> execute() {
        return this.participants;
    }
}
