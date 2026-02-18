package com.dnd.modutime.core.participant.controller;

import com.dnd.modutime.core.auth.oauth.OAuth2User;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.command.ParticipantJoinCommand;
import com.dnd.modutime.core.participant.controller.dto.ParticipantJoinRequest;
import com.dnd.modutime.core.participant.controller.dto.ParticipantsDeleteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ParticipantCommandController {

    private final ParticipantFacade participantFacade;

    public ParticipantCommandController(ParticipantFacade participantFacade) {
        this.participantFacade = participantFacade;
    }

    @PostMapping("/api/room/{roomUuid}/participants")
    public ResponseEntity<Void> joinAsOAuthUser(
            @PathVariable String roomUuid,
            @RequestBody @Valid ParticipantJoinRequest request,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        var command = ParticipantJoinCommand.of(
                roomUuid, request.name(), oAuth2User.user().getId());
        participantFacade.joinAsOAuthUser(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/room/{roomUuid}")
    public ResponseEntity<Void> deleteParticipants(@PathVariable String roomUuid,
                                                   @RequestBody @Valid ParticipantsDeleteRequest request) {
        participantFacade.delete(request.toCommand(roomUuid));
        return ResponseEntity.ok().build();
    }
}
