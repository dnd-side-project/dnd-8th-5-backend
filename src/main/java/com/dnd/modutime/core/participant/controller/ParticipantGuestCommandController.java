package com.dnd.modutime.core.participant.controller;

import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.controller.dto.ParticipantsDeleteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ParticipantGuestCommandController {

    private final ParticipantFacade participantFacade;

    public ParticipantGuestCommandController(ParticipantFacade participantFacade) {
        this.participantFacade = participantFacade;
    }

    @Deprecated(since = "카카오 로그인 배포 이후")
    @DeleteMapping("/guest/api/room/{roomUuid}")
    public ResponseEntity<Void> deleteParticipants(@PathVariable String roomUuid,
                                                   @RequestBody @Valid ParticipantsDeleteRequest request) {
        participantFacade.delete(request.toCommand(roomUuid));
        return ResponseEntity.ok().build();
    }
}
