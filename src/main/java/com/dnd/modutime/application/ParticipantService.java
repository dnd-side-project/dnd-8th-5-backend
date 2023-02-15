package com.dnd.modutime.application;

import com.dnd.modutime.domain.Participant;
import com.dnd.modutime.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public void create(String roomUuid, String name, String password) {
        Participant participant = new Participant(roomUuid, name, password);
        participantRepository.save(participant);
    }

    public boolean existsByName(String roomUuid, String name) {
        return participantRepository.existsByName(roomUuid, name);
    }

    public Participant getByRoomUuidAndName(String roomUuid, String name) {
        return participantRepository.findByRoomUuidAndName(roomUuid, name)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 참여자를 찾을 수 없습니다."));
    }
}
