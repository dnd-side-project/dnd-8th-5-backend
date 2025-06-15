package com.dnd.modutime.core.participant.controller;

import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.participant.controller.dto.ParticipantsDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantFacade participantFacade;

    @PostMapping("/api/room/{roomUuid}/email")
    public ResponseEntity<Void> registerEmail(@PathVariable String roomUuid,
                                              @RequestBody EmailCreationRequest emailCreationRequest) {
        participantFacade.registerEmail(roomUuid, emailCreationRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/room/{roomUuid}/email")
    public ResponseEntity<EmailResponse> getEmail(@PathVariable String roomUuid,
                                                  @RequestParam String name) {
        EmailResponse emailResponse = participantFacade.getEmail(roomUuid, name);
        return ResponseEntity.ok(emailResponse);
    }

    @DeleteMapping("/api/room/{roomUuid}")
    public ResponseEntity<Void> deleteParticipants(@PathVariable String roomUuid,
                                                   @RequestBody @Valid ParticipantsDeleteRequest request) {
        participantFacade.delete(request.toCommand(roomUuid));
        return ResponseEntity.ok().build();
    }
}
