package com.dnd.modutime.core.participant.application;

import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.participant.domain.Email;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantQueryService participantQueryService;

    public ParticipantService(ParticipantRepository participantRepository,
                              ParticipantQueryService participantQueryService) {
        this.participantRepository = participantRepository;
        this.participantQueryService = participantQueryService;
    }

    public void create(String roomUuid, String name, String password) {
        var participant = new Participant(roomUuid, name, password);
        participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String roomUuid, String name) {
        return participantQueryService.existsBy(roomUuid, name);
    }

    public void registerEmail(String roomUuid,
                              EmailCreationRequest emailCreationRequest) {
        var participant = getByRoomUuidAndName(roomUuid, emailCreationRequest.getName());
        participant.registerEmail(new Email(emailCreationRequest.getEmail()));
    }

    public EmailResponse getEmail(String roomUuid, String name) {
        var participant = participantQueryService.getByRoomUuidAndName(roomUuid, name);
        return EmailResponse.from(participant.getEmailOrNull());
    }

    @Transactional(readOnly = true)
    public Participant getByRoomUuidAndName(String roomUuid, String name) {
        return participantQueryService.getByRoomUuidAndName(roomUuid, name);
    }

    public void delete(String roomUuid, String name) {
        var participant = getByRoomUuidAndName(roomUuid, name);
        participantRepository.delete(participant);
    }

    public void delete(String roomUuid, List<String> participantNames) {
        var participants = participantQueryService.getByRoomUuidAndName(roomUuid, participantNames);
        participantRepository.deleteAll(participants);
    }
}
