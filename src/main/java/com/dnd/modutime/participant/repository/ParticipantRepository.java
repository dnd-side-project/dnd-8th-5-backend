package com.dnd.modutime.participant.repository;

import com.dnd.modutime.participant.domain.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByRoomUuidAndName(String roomUuid, String name);
    boolean existsByRoomUuidAndName(String roomUuid, String name);
    List<Participant> findByRoomUuid(String roomUuid);
}
