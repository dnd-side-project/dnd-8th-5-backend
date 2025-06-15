package com.dnd.modutime.core.participant.controller;

import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.controller.dto.ParticipantsDeleteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ParticipantCommandController {

    private final ParticipantFacade participantFacade;

    public ParticipantCommandController(ParticipantFacade participantFacade) {
        this.participantFacade = participantFacade;
    }

    @PostMapping("/api/room/{roomUuid}/email")
    public ResponseEntity<Void> registerEmail(@PathVariable String roomUuid,
                                              @RequestBody EmailCreationRequest emailCreationRequest) {
        participantFacade.registerEmail(roomUuid, emailCreationRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/room/{roomUuid}")
    public ResponseEntity<Void> deleteParticipants(@PathVariable String roomUuid,
                                                   @RequestBody @Valid ParticipantsDeleteRequest request) {
        participantFacade.delete(request.toCommand(roomUuid));
        return ResponseEntity.ok().build();
    }
}
