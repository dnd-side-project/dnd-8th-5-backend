package com.dnd.modutime.adjustresult.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateTimeInfoDto {

    private final LocalDateTime dateTime;
    private final List<String> participantNames;
}
