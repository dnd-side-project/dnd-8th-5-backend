package com.dnd.modutime.core.auth.controller;

import com.dnd.modutime.core.auth.application.GuestAuthFacade;
import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.auth.application.response.GuestLoginResponse;
import com.dnd.modutime.core.auth.application.response.LoginPageResponse;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.room.application.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthGuestController {

    private final ParticipantFacade participantFacade;
    private final RoomService roomService;
    private final GuestAuthFacade guestAuthFacade;

    public AuthGuestController(ParticipantFacade participantFacade,
                               RoomService roomService,
                               GuestAuthFacade guestAuthFacade) {
        this.participantFacade = participantFacade;
        this.roomService = roomService;
        this.guestAuthFacade = guestAuthFacade;
    }

    @Deprecated(since = "카카오 로그인 배포 이후")
    @PostMapping("/guest/api/room/{roomUuid}/login")
    public ResponseEntity<Void> login(@PathVariable String roomUuid,
                                      @RequestBody LoginRequest loginRequest) {
        participantFacade.login(loginRequest.toParticipantCreateCommand(roomUuid));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/guest/api/v1/room/{roomUuid}/login")
    public ResponseEntity<GuestLoginResponse> loginGuestV1(@PathVariable String roomUuid,
                                                           @RequestBody LoginRequest loginRequest) {
        var response = guestAuthFacade.login(loginRequest.toParticipantCreateCommand(roomUuid));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guest/api/room/{roomUuid}/login")
    public ResponseEntity<LoginPageResponse> loginPage(@PathVariable String roomUuid) {
        String roomName = roomService.getTitleByUuid(roomUuid);
        return ResponseEntity.ok(new LoginPageResponse(roomName));
    }
}
