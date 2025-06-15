package com.dnd.modutime.core.participant.controller.dto;

import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;

import javax.validation.constraints.NotNull;
import java.util.List;

public record ParticipantsDeleteRequest(
        @NotNull(message = "참여자 ids 는 필수값 입니다.")
        List<Long> participantIds
) {
    public ParticipantsDeleteCommand toCommand(String roomUuid) {
        return ParticipantsDeleteCommand.of(roomUuid, this.participantIds);
    }
}
