package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.participant.domain.Email;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import com.dnd.modutime.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public void create(String roomUuid, String name, String password) {
        Participant participant = new Participant(roomUuid, name, password);
        participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String roomUuid, String name) {
        return participantRepository.existsByRoomUuidAndName(roomUuid, name);
    }

    public void registerEmail(String roomUuid,
                              EmailCreationRequest emailCreationRequest) {
        Participant participant = getByRoomUuidAndName(roomUuid, emailCreationRequest.getName());
        participant.registerEmail(new Email(emailCreationRequest.getEmail()));
    }

    public EmailResponse getEmail(String roomUuid, String name) {
        Participant participant = getByRoomUuidAndName(roomUuid, name);
        return EmailResponse.from(participant.getEmailOrNull());
    }

    @Transactional(readOnly = true)
    public Participant getByRoomUuidAndName(String roomUuid, String name) {
        return participantRepository.findByRoomUuidAndName(roomUuid, name)
                .orElseThrow(() -> new NotFoundException("해당하는 참여자를 찾을 수 없습니다."));
    }

    public void delete(String roomUuid, String name) {
        var participant = getByRoomUuidAndName(roomUuid, name);
        participantRepository.delete(participant);
    }

    public void delete(String roomUuid, List<String> participantNames) {
        var participants = getByRoomUuidAndName(roomUuid, participantNames);
        participantRepository.deleteAll(participants);
    }

    private List<Participant> getByRoomUuidAndName(String roomUuid, List<String> participantNames) {
        return participantRepository.findByRoomUuidAndNameIn(roomUuid, participantNames);
    }
}
