package com.dnd.modutime.repository;

import com.dnd.modutime.domain.participant.Participant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ParticipantRepository {

    private Long id = 0L;
    private final Map<Long, Participant> store = new HashMap<>();

    public Participant save(Participant participant) {
        store.put(id, participant);
        return new Participant(id++,
                participant.getRoomUuid(),
                participant.getName(),
                participant.getPassword(),
                participant.getEmail());
    }

    public Optional<Participant> findByRoomUuidAndName(String roomUuid, String name) {
        return store.values().stream()
                .filter(participant -> participant.getRoomUuid().equals(roomUuid) && participant.getName().equals(name))
                .findAny();
    }

    public boolean existsByName(String roomUuid, String name) {
        return store.values().stream()
                .anyMatch(participant -> participant.getRoomUuid().equals(roomUuid) && participant.getName().equals(name));
    }

    public List<Participant> findByRoomUuid(String roomUuid) {
        return store.values().stream()
                .filter(participant -> participant.getRoomUuid().equals(roomUuid))
                .collect(Collectors.toList());
    }
}
