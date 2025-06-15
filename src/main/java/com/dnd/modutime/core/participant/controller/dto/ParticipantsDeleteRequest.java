package com.dnd.modutime.core.participant.controller.dto;

import java.util.List;

public record ParticipantsDeleteRequest(
        List<String> participantNames
) {
}
