package com.dnd.modutime.core.timeblock.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantCreationEvent {

    private String roomUuid;
    private String name;
}
