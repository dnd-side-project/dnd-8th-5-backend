package com.dnd.modutime.domain.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantCreationEvent {

    private String roomUuid;
    private String name;
}
