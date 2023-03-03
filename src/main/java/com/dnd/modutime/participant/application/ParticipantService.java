package com.dnd.modutime.participant.application;

import com.dnd.modutime.dto.request.EmailCreationRequest;
import com.dnd.modutime.dto.response.EmailResponse;
import com.dnd.modutime.participant.domain.Email;
import com.dnd.modutime.participant.domain.Participant;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
