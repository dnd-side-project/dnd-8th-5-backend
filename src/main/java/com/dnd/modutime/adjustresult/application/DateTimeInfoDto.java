package com.dnd.modutime.adjustresult.application;

import java.time.LocalDateTime;
import java.util.List;

public class DateTimeInfoDto {

    private final LocalDateTime dateTime;
    private final List<String> participantNames;

    public DateTimeInfoDto(LocalDateTime dateTime, List<String> participantNames) {
        this.dateTime = dateTime;
        this.participantNames = participantNames;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<String> getParticipantNames() {
        return participantNames;
    }
}
