package com.dnd.modutime.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdjustmentResultResponse {

    @JsonProperty(value = "candidateTimes")
    private List<CandidateTimeResponse> candidateTimeResponses;
}
