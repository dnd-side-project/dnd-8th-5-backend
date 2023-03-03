package com.dnd.modutime.core.auth.controller;

import com.dnd.modutime.core.auth.application.AuthService;
import com.dnd.modutime.core.auth.application.request.LoginRequest;
import com.dnd.modutime.core.auth.application.response.LoginPageResponse;
import com.dnd.modutime.core.room.application.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room/{roomUuid}/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Void> login(@PathVariable String roomUuid,
                                      @RequestBody LoginRequest loginRequest) {
        authService.login(roomUuid, loginRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<LoginPageResponse> loginPage(@PathVariable String roomUuid) {
        String roomName = roomService.getTitleByUuid(roomUuid);
        return ResponseEntity.ok(new LoginPageResponse(roomName));
    }
}
