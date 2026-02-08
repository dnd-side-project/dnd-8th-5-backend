package com.dnd.modutime.core.adjustresult.controller;

import com.dnd.modutime.core.Page;
import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponseV1;
import com.dnd.modutime.core.adjustresult.controller.dto.AdjustmentResultRequest;
import com.dnd.modutime.infrastructure.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdjustmentResultGuestController {

    private final AdjustmentResultService adjustmentResultService;

    @GetMapping("/guest/api/room/{roomUuid}/adjustment-result")
    public ResponseEntity<AdjustmentResultResponse> getAdjustmentResult(@PathVariable String roomUuid,
                                                                        @RequestParam(defaultValue = "fast") String sorted,
                                                                        @RequestParam(value = "name", defaultValue = "") List<String> names) {
        AdjustmentResultResponse adjustmentResultResponse = adjustmentResultService.getByRoomUuidAndSortedAndNames(
                roomUuid, sorted, names
        );
        return ResponseEntity.ok(adjustmentResultResponse);
    }

    @GetMapping("/guest/api/v1/room/{roomUuid}/adjustment-results")
    public Page<CandidateDateTimeResponseV1> v1getAdjustmentResult(@PathVariable String roomUuid,
                                                                   @Valid AdjustmentResultRequest request,
                                                                   PageRequest pageRequest) {
        return this.adjustmentResultService.search(request.toSearchCondition(roomUuid), pageRequest);
    }
}
