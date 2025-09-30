package com.dnd.modutime.core.adjustresult.controller;

import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdjustmentResultController {

    private final AdjustmentResultService adjustmentResultService;

    @GetMapping("/api/room/{roomUuid}/adjustment-result")
    public ResponseEntity<AdjustmentResultResponse> getAdjustmentResult(@PathVariable String roomUuid,
                                                                        @RequestParam(defaultValue = "fast") String sorted,
                                                                        @RequestParam(value = "name", defaultValue = "") List<String> names) {
        AdjustmentResultResponse adjustmentResultResponse = adjustmentResultService.getByRoomUuidAndSortedAndNames(
                roomUuid, sorted, names
        );
        return ResponseEntity.ok(adjustmentResultResponse);
    }

    @GetMapping("/api/v1/room/{roomUuid}/adjustment-results")
    public ResponseEntity<AdjustmentResultResponse> v1getAdjustmentResult(@PathVariable String roomUuid,
                                                                          @RequestParam(defaultValue = "fast") String sorted,
                                                                          @RequestParam(value = "name", defaultValue = "") List<String> participantNames) {
        var adjustmentResultResponse = adjustmentResultService.v1getByRoomUuidAndSortedAndNames(
                roomUuid, sorted, participantNames
        );
        return ResponseEntity.ok(adjustmentResultResponse);
    }
}
