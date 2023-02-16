package com.dnd.modutime.domain.timeblock;

import java.util.List;

public class TimeBlock {

    private Long id;
    private String roomUuid;
    private String participantName;
    private AvailableDateTimeValidator availableDateTimeValidator;
    private List<AvailableDateTime> availableDateTimes = List.of();

    public TimeBlock(String roomUuid,
                     String participantName,
                     AvailableDateTimeValidator availableDateTimeValidator) {
        validateRoomUuid(roomUuid);
        validateParticipantName(participantName);

        this.roomUuid = roomUuid;
        this.participantName = participantName;
        this.availableDateTimeValidator = availableDateTimeValidator;
    }

    private void validateParticipantName(String participantName) {
        if (participantName == null) {
            throw new IllegalArgumentException("participantName은 null일 수 없습니다.");
        }
    }

    private void validateRoomUuid(String roomUuid) {
        if (roomUuid == null) {
            throw new IllegalArgumentException("roomUuid는 null일 수 없습니다.");
        }
    }

    public List<AvailableDateTime> getAvailableDateTimes() {
        return availableDateTimes;
    }

    public void replace(List<AvailableDateTime> availableDateTimes) {
        availableDateTimeValidator.validate(roomUuid, availableDateTimes);
        this.availableDateTimes = availableDateTimes;
    }
}
