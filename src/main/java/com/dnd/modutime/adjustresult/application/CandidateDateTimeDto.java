package com.dnd.modutime.adjustresult.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CandidateDateTimeDto {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final List<String> participantNames;

    public CandidateDateTimeDto(LocalDate date,
                                LocalTime startTime,
                                LocalTime endTime,
                                List<String> participantNames) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantNames = participantNames;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public List<String> getParticipantNames() {
        return participantNames;
    }
}
