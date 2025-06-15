package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import com.dnd.modutime.core.participant.application.command.ParticipantsDeleteCommand;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipantCommandHandler {

    private final ParticipantRepository participantRepository;

    public ParticipantCommandHandler(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public void handle(ParticipantsDeleteCommand command) {
        var participants = command.execute();
        for (Participant participant : participants) {
            participantRepository.delete(participant);
        }
    }

    @Transactional
    public Participant handle(ParticipantCreateCommand command) {
        return participantRepository.save(command.execute());
    }
}
