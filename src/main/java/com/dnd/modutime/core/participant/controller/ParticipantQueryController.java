package com.dnd.modutime.core.participant.controller;

import com.dnd.modutime.core.auth.application.RoomParticipant;
import com.dnd.modutime.core.participant.controller.dto.RoomParticipantsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParticipantQueryController {

    @GetMapping("/api/v1/rooms/{roomUuid}/participants/me")
    public RoomParticipantsResponse getMe(@RoomParticipant String participantName){
        return new RoomParticipantsResponse(participantName);
    }
}
