package com.dnd.modutime.controller;

import com.dnd.modutime.application.ParticipantService;
import com.dnd.modutime.dto.request.ParticipantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{roomUuid}/participant")
    public ResponseEntity<Void> create(@PathVariable String roomUuid,
                                       @RequestBody ParticipantRequest participantRequestRequest) {
        participantService.create(roomUuid, participantRequestRequest);
        return ResponseEntity.ok().build();
    }
}
