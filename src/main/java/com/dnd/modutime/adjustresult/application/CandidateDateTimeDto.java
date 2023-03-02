package com.dnd.modutime.adjustresult.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandidateDateTimeDto {

    private final Long id;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final List<String> participantNames;
    private final boolean isConfirmed;
}
