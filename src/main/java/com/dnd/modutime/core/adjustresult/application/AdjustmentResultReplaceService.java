package com.dnd.modutime.core.adjustresult.application;

import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.adjustresult.repository.CandidateDateTimeRepository;
import com.dnd.modutime.core.room.util.CandidateDateTimeConvertorFactory;
import com.dnd.modutime.core.adjustresult.util.convertor.CandidateDateTimeConvertor;
import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfoParticipantName;
import com.dnd.modutime.core.timetable.domain.TimeTableReplaceEvent;
import com.dnd.modutime.exception.NotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final CandidateDateTimeConvertorFactory candidateDateTimeConvertorFactory;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void replace(TimeTableReplaceEvent event) {
        AdjustmentResult adjustmentResult = getByRoomUuid(event.getRoomUuid());
        candidateDateTimeRepository.deleteAllByAdjustmentResultId(adjustmentResult.getId());
        List<DateTimeInfoDto> dateTimeInfosDto = convertDateTimeInfosDto(event.getDateInfos());
        CandidateDateTimeConvertor candidateDateTimeConvertor = candidateDateTimeConvertorFactory.getInstance(event.getRoomUuid());
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
                addDateTimeInfoDto(dateTimeInfosDto, dateInfo, timeInfo);
            });
        }
        dateTimeInfosDto.sort(Comparator.comparing(DateTimeInfoDto::getDateTime));
        return dateTimeInfosDto;
    }

    private void addDateTimeInfoDto(List<DateTimeInfoDto> dateTimeInfosDto,
                                    DateInfo dateInfo,
                                    TimeInfo timeInfo) {
        dateTimeInfosDto.add(new DateTimeInfoDto(LocalDateTime.of(dateInfo.getDate(), getTimeOrZeroTime(timeInfo)),
                timeInfo.getTimeInfoParticipantNames().stream()
                        .map(TimeInfoParticipantName::getName)
                        .collect(Collectors.toList())));
    }

    private LocalTime getTimeOrZeroTime(TimeInfo timeInfo) {
        if (timeInfo.hasTime()) {
            return timeInfo.getTime();
        }
        return LocalTime.of(0, 0);
    }
}
