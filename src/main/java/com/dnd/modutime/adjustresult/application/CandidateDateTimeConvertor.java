package com.dnd.modutime.adjustresult.application;

import com.dnd.modutime.timetable.domain.DateInfo;
import com.dnd.modutime.timetable.domain.TimeTableParticipantName;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CandidateDateTimeConvertor {
    public List<CandidateDateTimeDto> convert(List<DateTimeInfoDto> dateTimeInfosDto) {
//        List<DateTimeInfoDto> dateTimeInfosDto = convertDateTimeInfosDto(dateInfos);
        List<CandidateDateTimeDto> candidateDateTimesDto = new ArrayList<>();
        DateTimeInfoDto firstDateTimeInfoDto = dateTimeInfosDto.get(0);
        LocalDateTime preDateTime = firstDateTimeInfoDto.getDateTime();
        LocalTime preStartTime = preDateTime.toLocalTime();
        LocalTime preEndTime = preStartTime.plusMinutes(30);
        List<String> preParticipantNames = firstDateTimeInfoDto.getParticipantNames();

        for (int i = 1; i < dateTimeInfosDto.size(); i++) {
            DateTimeInfoDto currentDateTimeInfoDto = dateTimeInfosDto.get(i);

            List<String> currentParticipantNames = currentDateTimeInfoDto.getParticipantNames();
            LocalDateTime currentLocalDateTime = currentDateTimeInfoDto.getDateTime();

            if (isContinuousTime(preDateTime, currentLocalDateTime, preParticipantNames, currentParticipantNames)) {
                preEndTime = currentDateTimeInfoDto.getDateTime().toLocalTime().plusMinutes(30);
                continue;
            }

            if (preParticipantNames.size() > 0) {
                candidateDateTimesDto.add(new CandidateDateTimeDto(preDateTime.toLocalDate(), preStartTime, preEndTime, preParticipantNames));
            }

            preStartTime = currentDateTimeInfoDto.getDateTime().toLocalTime();
            preEndTime = preStartTime.plusMinutes(30);
            preParticipantNames = currentDateTimeInfoDto.getParticipantNames();
            preDateTime = currentLocalDateTime;
        }

        candidateDateTimesDto.add(
                new CandidateDateTimeDto(preDateTime.toLocalDate(), preStartTime, preEndTime, preParticipantNames));
        return candidateDateTimesDto;
    }

    private List<DateTimeInfoDto> convertDateTimeInfosDto(List<DateInfo> dateInfos) {
        List<DateTimeInfoDto> dateTimeInfosDto = new ArrayList<>();
        for (DateInfo dateInfo : dateInfos) {
            dateInfo.getTimeInfos().forEach(timeInfo -> {
                dateTimeInfosDto.add(new DateTimeInfoDto(LocalDateTime.of(dateInfo.getDate(), timeInfo.getTime()),
                        timeInfo.getTimeTableParticipantNames().stream()
                                .map(TimeTableParticipantName::getName)
                                .collect(Collectors.toList())));
            });
        }
        dateTimeInfosDto.sort(Comparator.comparing(DateTimeInfoDto::getDateTime));
        return dateTimeInfosDto;
    }

    private boolean isContinuousTime(LocalDateTime preDateTime,
                                     LocalDateTime currentLocalDateTime,
                                     List<String> preParticipantNames,
                                     List<String> currentParticipantNames) {
        if (isSameSize(preParticipantNames, currentParticipantNames)) {
            return isContinuousTerm(preDateTime, currentLocalDateTime)
                    && isSameNames(preParticipantNames, currentParticipantNames);
        }
        return false;
    }

    private boolean isSameSize(List<String> preParticipantNames, List<String> currentParticipantNames) {
        return preParticipantNames.size() == currentParticipantNames.size();
    }

    private boolean isContinuousTerm(LocalDateTime preLocalDateTime, LocalDateTime currentLocalDateTime) {
        return preLocalDateTime.plusMinutes(30).equals(currentLocalDateTime);
    }

    private boolean isSameNames(List<String> preParticipantNames, List<String> currentParticipantNames) {
        return currentParticipantNames.containsAll(preParticipantNames);
    }
}


