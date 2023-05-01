package com.dnd.modutime.core.adjustresult.util.convertor;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DateTimeRoomConvertor implements CandidateDateTimeConvertor {

    @Override
    public List<CandidateDateTime> convert(List<DateTimeInfoDto> dateTimeInfosDto) {
        List<CandidateDateTime> candidateDateTimes = new ArrayList<>();
        DateTimeInfoDto firstDateTimeInfoDto = dateTimeInfosDto.get(0);

        LocalDateTime preStartDateTime = firstDateTimeInfoDto.getDateTime();
        LocalDateTime preEndDateTime = preStartDateTime.plusMinutes(30);
        List<String> preParticipantNames = firstDateTimeInfoDto.getParticipantNames();

        for (int i = 1; i < dateTimeInfosDto.size(); i++) {
            DateTimeInfoDto currentDateTimeInfoDto = dateTimeInfosDto.get(i);

            List<String> currentParticipantNames = currentDateTimeInfoDto.getParticipantNames();
            LocalDateTime currentStartDateTime = currentDateTimeInfoDto.getDateTime();

            if (isContinuousTime(preStartDateTime, currentStartDateTime, preParticipantNames, currentParticipantNames)) {
                preEndDateTime = currentDateTimeInfoDto.getDateTime().plusMinutes(30);
                continue;
            }

            if (preParticipantNames.size() > 0) {
                addCandidateTime(candidateDateTimes, preStartDateTime, preEndDateTime, preParticipantNames);
            }

            preStartDateTime = currentStartDateTime;
            preEndDateTime = preStartDateTime.plusMinutes(30);
            preParticipantNames = currentDateTimeInfoDto.getParticipantNames();
        }

        addCandidateTime(candidateDateTimes, preStartDateTime, preEndDateTime, preParticipantNames);
        return candidateDateTimes;
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

    private void addCandidateTime(List<CandidateDateTime> candidateDateTimes,
                                  LocalDateTime preStartDateTime,
                                  LocalDateTime preEndDateTime,
                                  List<String> preParticipantNames) {
        candidateDateTimes.add(new CandidateDateTime(null, preStartDateTime, preEndDateTime, null,
                preParticipantNames.stream()
                        .map(CandidateDateTimeParticipantName::new)
                        .collect(Collectors.toList())));
    }
}


