package com.dnd.modutime.core.participant.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantQueryRepository {

    @Query("select count(p) from Participant p")
    long countAll();

    long countByUserIdIsNull();

    @Query("select p.createdAt from Participant p where p.createdAt >= :from")
    List<LocalDateTime> findCreatedAtsAfter(@Param("from") LocalDateTime from);

    @Query("select p.createdAt from Participant p where p.userId is null and p.createdAt >= :from")
    List<LocalDateTime> findGuestCreatedAtsAfter(@Param("from") LocalDateTime from);

    Optional<Participant> findByRoomUuidAndName(String roomUuid, String name);

    boolean existsByRoomUuidAndName(String roomUuid, String name);

    boolean existsByRoomUuidAndUserId(String roomUuid, Long userId);

    Optional<Participant> findByRoomUuidAndUserId(String roomUuid, Long userId);

    List<Participant> findByRoomUuid(String roomUuid);

    List<Participant> findByRoomUuidAndNameIn(String roomUuid, List<String> participantNames);

    List<Participant> findByRoomUuidAndIdIn(String roomUuid, List<Long> participantIds);
}
