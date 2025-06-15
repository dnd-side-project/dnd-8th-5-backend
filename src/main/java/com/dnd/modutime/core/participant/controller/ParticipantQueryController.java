package com.dnd.modutime.core.participant.controller;

import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParticipantQueryController {

    private final ParticipantFacade participantFacade;

    public ParticipantQueryController(ParticipantFacade participantFacade) {
        this.participantFacade = participantFacade;
    }

    @GetMapping("/api/room/{roomUuid}/email")
    public ResponseEntity<EmailResponse> getEmail(@PathVariable String roomUuid,
                                                  @RequestParam String name) {
        EmailResponse emailResponse = participantFacade.getEmail(roomUuid, name);
        return ResponseEntity.ok(emailResponse);
    }
}
