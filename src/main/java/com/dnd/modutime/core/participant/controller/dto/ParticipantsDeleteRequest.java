package com.dnd.modutime.core.participant.controller.dto;

import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;

import java.util.List;

public record ParticipantsDeleteRequest(
        List<String> participantNames
) {
    public ParticipantsDeleteCommand toCommand(String roomUuid) {
        return ParticipantsDeleteCommand.of(roomUuid, this.participantNames);
    }
}
