package com.dnd.modutime.core.participant.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantDeletionEvent {

    private String roomUuid;
    private String name;
}