package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.domain.ParticipantQueryRepository;
import com.dnd.modutime.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ParticipantQueryService {

    private final ParticipantQueryRepository queryRepository;

    public ParticipantQueryService(ParticipantQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public List<Participant> getByRoomUuidAndName(String roomUuid, List<String> participantNames) {
        return queryRepository.findByRoomUuidAndNameIn(roomUuid, participantNames);
    }

    public Participant getByRoomUuidAndName(String roomUuid, String name) {
        return queryRepository.findByRoomUuidAndName(roomUuid, name)
                .orElseThrow(() -> new NotFoundException("해당하는 참여자를 찾을 수 없습니다."));
    }

    public boolean existsBy(String roomUuid, String name) {
        return queryRepository.existsByRoomUuidAndName(roomUuid, name);
    }
}
