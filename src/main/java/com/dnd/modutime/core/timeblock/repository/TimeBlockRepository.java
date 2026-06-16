package com.dnd.modutime.core.timeblock.repository;

import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeBlockRepository extends JpaRepository<TimeBlock, Long> {

    Optional<TimeBlock> findByRoomUuidAndParticipantName(String roomUuid, String participantName);

    List<TimeBlock> findByRoomUuid(String roomUuid);

    boolean existsByRoomUuid(String roomUuid);

    /**
     * 최근 활성 방 수 — modifiedAt(참여자 시간 블록의 최종 수정 시각)이 기준 시각 이후인 방의 distinct 개수.
     */
    @Query("select count(distinct t.roomUuid) from TimeBlock t where t.modifiedAt >= :from")
    long countActiveRoomsSince(@Param("from") LocalDateTime from);
}
