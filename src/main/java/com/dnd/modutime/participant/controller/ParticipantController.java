package com.dnd.modutime.participant.controller;

import com.dnd.modutime.dto.request.EmailCreationRequest;
import com.dnd.modutime.participant.application.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room/{roomUuid}")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/email")
    public ResponseEntity<Void> registerEmail(@PathVariable String roomUuid,
                                              @RequestBody EmailCreationRequest emailCreationRequest) {
        participantService.registerEmail(roomUuid, emailCreationRequest);
        return ResponseEntity.ok().build();
    }
}
