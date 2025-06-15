package com.dnd.modutime.core.participant.domain;

import java.util.List;
import java.util.Optional;

public interface ParticipantQueryRepository {
    Optional<Participant> findByRoomUuidAndName(String roomUuid, String name);

    boolean existsByRoomUuidAndName(String roomUuid, String name);

    List<Participant> findByRoomUuid(String roomUuid);

    List<Participant> findByRoomUuidAndNameIn(String roomUuid, List<String> participantNames);

    List<Participant> findByRoomUuidAndIdIn(String roomUuid, List<Long> participantIds);
}
