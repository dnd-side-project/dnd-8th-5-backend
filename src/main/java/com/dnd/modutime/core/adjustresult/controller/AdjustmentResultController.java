package com.dnd.modutime.core.adjustresult.controller;

import com.dnd.modutime.core.Page;
import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponseV1;
import com.dnd.modutime.core.adjustresult.controller.dto.AdjustmentResultRequest;
import com.dnd.modutime.infrastructure.PageRequest;
import java.util.List;
import javax.validation.Valid;
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

    @Deprecated(since = "카카오 로그인 배포 이후")
    @GetMapping("/api/v1/room/{roomUuid}/adjustment-results")
    public Page<CandidateDateTimeResponseV1> v1getAdjustmentResult(@PathVariable String roomUuid,
                                                                   @Valid AdjustmentResultRequest request,
                                                                   PageRequest pageRequest) {
        return this.adjustmentResultService.search(request.toSearchCondition(roomUuid), pageRequest);
    }
}
