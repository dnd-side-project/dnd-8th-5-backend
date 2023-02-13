package com.dnd.modutime.repository;

import com.dnd.modutime.domain.Participant;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ParticipantRepository {

    private Long id = 0L;
    private final Map<Long, Participant> store = new HashMap<>();

    public void save(Participant participant) {
        store.put(id++, participant);
    }

    public Optional<Participant> findByRoomUuidAndName(String roomUuid, String name) {
        return store.values().stream()
                .filter(participant -> participant.getRoomUuid().equals(roomUuid) && participant.getName().equals(name))
                .findAny();
    }
}
