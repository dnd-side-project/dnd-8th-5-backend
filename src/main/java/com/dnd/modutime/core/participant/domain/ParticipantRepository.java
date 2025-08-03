package com.dnd.modutime.core.participant.domain;

public interface ParticipantRepository {
    void delete(Participant participant);

    Participant save(Participant participant);
}
