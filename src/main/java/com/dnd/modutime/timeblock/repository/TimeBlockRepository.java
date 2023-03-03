package com.dnd.modutime.timeblock.repository;

import com.dnd.modutime.timeblock.domain.TimeBlock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeBlockRepository extends JpaRepository<TimeBlock, Long> {

    Optional<TimeBlock> findByRoomUuidAndParticipantName(String roomUuid, String participantName);

    List<TimeBlock> findByRoomUuid(String roomUuid);
}
