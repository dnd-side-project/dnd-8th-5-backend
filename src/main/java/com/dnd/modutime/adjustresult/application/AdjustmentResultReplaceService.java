package com.dnd.modutime.adjustresult.application;

import com.dnd.modutime.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.adjustresult.repository.CandidateDateTimeRepository;
import com.dnd.modutime.adjustresult.util.convertor.CandidateDateTimeConvertor;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.timetable.domain.DateInfo;
import com.dnd.modutime.timetable.domain.TimeInfoParticipantName;
import com.dnd.modutime.timetable.domain.TimeTableReplaceEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class AdjustmentResultReplaceService {

    private final AdjustmentResultRepository adjustmentResultRepository;
    private final CandidateDateTimeRepository candidateDateTimeRepository;
    private final CandidateDateTimeConvertor candidateDateTimeConvertor;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void replace(TimeTableReplaceEvent event) {
        AdjustmentResult adjustmentResult = getByRoomUuid(event.getRoomUuid());
        candidateDateTimeRepository.deleteAllByAdjustmentResultId(adjustmentResult.getId());
        List<DateTimeInfoDto> dateTimeInfosDto = convertDateTimeInfosDto(event.getDateInfos());
        List<CandidateDateTime> candidateDateTimes = candidateDateTimeConvertor.convert(dateTimeInfosDto);
        candidateDateTimes.forEach(candidateDateTime -> candidateDateTime.makeEntity(adjustmentResult));
        candidateDateTimeRepository.saveAll(candidateDateTimes);
        adjustmentResult.replace(candidateDateTimes);
    }

    private AdjustmentResult getByRoomUuid(String roomUuid) {
        return adjustmentResultRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("roomUuid에 해당하는 조율 결과가 없습니다."));
    }

    private List<DateTimeInfoDto> convertDateTimeInfosDto(List<DateInfo> dateInfos) {
        List<DateTimeInfoDto> dateTimeInfosDto = new ArrayList<>();
        for (DateInfo dateInfo : dateInfos) {
            dateInfo.getTimeInfos().forEach(timeInfo -> {
                dateTimeInfosDto.add(new DateTimeInfoDto(LocalDateTime.of(dateInfo.getDate(), timeInfo.getTime()),
                        timeInfo.getTimeInfoParticipantNames().stream()
                                .map(TimeInfoParticipantName::getName)
                                .collect(Collectors.toList())));
            });
        }
        dateTimeInfosDto.sort(Comparator.comparing(DateTimeInfoDto::getDateTime));
        return dateTimeInfosDto;
    }
}
