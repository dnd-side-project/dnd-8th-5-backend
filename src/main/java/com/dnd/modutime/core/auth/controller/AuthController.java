package com.dnd.modutime.core.auth.controller;

import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.auth.application.response.LoginPageResponse;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.room.application.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final ParticipantFacade participantFacade;
    private final RoomService roomService;

    public AuthController(ParticipantFacade participantFacade, RoomService roomService) {
        this.participantFacade = participantFacade;
        this.roomService = roomService;
    }

    @Deprecated(since = "카카오 로그인 배포 이후")
    @PostMapping("/api/room/{roomUuid}/login")
    public ResponseEntity<Void> login(@PathVariable String roomUuid,
                                      @RequestBody LoginRequest loginRequest) {
        participantFacade.login(loginRequest.toParticipantCreateCommand(roomUuid));
        return ResponseEntity.ok().build();
    }

    @Deprecated(since = "카카오 로그인 배포 이후")
    @GetMapping("/api/room/{roomUuid}/login")
    public ResponseEntity<LoginPageResponse> loginPage(@PathVariable String roomUuid) {
        String roomName = roomService.getTitleByUuid(roomUuid);
        return ResponseEntity.ok(new LoginPageResponse(roomName));
    }
}
