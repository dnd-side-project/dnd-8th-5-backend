package com.dnd.modutime.core.timeblock.controller;


import com.dnd.modutime.core.auth.application.ParticipantInfo;
import com.dnd.modutime.core.auth.application.RoomParticipant;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequestV1;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimeBlockController {

    private final TimeBlockService timeBlockService;

    @Deprecated(since = "카카오 로그인 배포 이후")
    @PutMapping("/api/room/{roomUuid}/available-time")
    public ResponseEntity<Void> replace(@PathVariable String roomUuid,
                                        @RequestBody TimeReplaceRequest timeReplaceRequest) {
        timeBlockService.replace(roomUuid, timeReplaceRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/v1/rooms/{roomUuid}/time-blocks/available-time")
    public ResponseEntity<Void> replaceV1(@PathVariable String roomUuid,
                                          @RequestBody TimeReplaceRequestV1 request,
                                          @RoomParticipant ParticipantInfo participantInfo) {
        timeBlockService.replaceV1(request.toCommand(roomUuid, participantInfo.participantName()));
        return ResponseEntity.ok().build();
    }

    @Deprecated(since = "카카오 로그인 배포 이후")
    @GetMapping("/api/room/{roomUuid}/available-time")
    public ResponseEntity<TimeBlockResponse> getTimeBlock(@PathVariable String roomUuid,
                                                          @RequestParam String name) {
        var timeBlockResponse = timeBlockService.getTimeBlock(roomUuid, name);
        return ResponseEntity.ok(timeBlockResponse);
    }
}
