package com.dnd.modutime.core.participant.repository;

import com.dnd.modutime.core.participant.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByRoomUuidAndName(String roomUuid, String name);

    boolean existsByRoomUuidAndName(String roomUuid, String name);

    List<Participant> findByRoomUuid(String roomUuid);

    List<Participant> findByRoomUuidAndNameIn(String roomUuid, List<String> participantNames);
}