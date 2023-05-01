package com.dnd.modutime.core.adjustresult.util.convertor;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DateRoomConvertor implements CandidateDateTimeConvertor {

    @Override
    public List<CandidateDateTime> convert(List<DateTimeInfoDto> dateTimeInfosDto) {
        List<CandidateDateTime> candidateDateTimes = new ArrayList<>();
        for (DateTimeInfoDto dateTimeInfoDto : dateTimeInfosDto) {
            List<String> participantNames = dateTimeInfoDto.getParticipantNames();
            candidateDateTimes.add(new CandidateDateTime(
                    null,
                    dateTimeInfoDto.getDateTime(),
                    dateTimeInfoDto.getDateTime(),
                    null,
                    participantNames.stream()
                    .map(CandidateDateTimeParticipantName::new)
                    .collect(Collectors.toList())));
        }
        return candidateDateTimes;
    }
}
