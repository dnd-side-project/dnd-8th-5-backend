package com.dnd.modutime.application;

import com.dnd.modutime.domain.Participant;
import com.dnd.modutime.dto.request.ParticipantRequest;
import com.dnd.modutime.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public void create(String roomUuid, ParticipantRequest participantRequest) {
        Participant participant = new Participant(roomUuid, participantRequest.getName(), participantRequest.getEmail());
        participantRepository.save(participant);
    }
}
