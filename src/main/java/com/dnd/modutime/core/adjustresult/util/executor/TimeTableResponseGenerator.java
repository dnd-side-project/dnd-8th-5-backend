package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.adjustresult.util.convertor.CandidateDateTimeConvertor;
import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorter;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorterFactory;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeTableResponseGenerator implements AdjustmentResultResponseGenerator {

    private final TimeTableRepository timeTableRepository;
    private final CandidateDateTimeConvertor candidateDateTimeConvertor;
    private final CandidateDateTimesSorterFactory candidateDateTimesSorterFactory;

    @Override
    public AdjustmentResultResponse generate(String roomUuid,
                                             CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                             List<String> names) {
        TimeTable timeTable = getTimeTableByRoomUuid(roomUuid);
        List<DateTimeInfoDto> dateTimeInfosDto = timeTable.getDateTimeInfosDtoByParticipantNames(names);
        List<CandidateDateTime> candidateDateTimes = candidateDateTimeConvertor.convert(dateTimeInfosDto);
        CandidateDateTimesSorter candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        return AdjustmentResultResponse.from(candidateDateTimes.stream()
                .limit(5)
                .collect(Collectors.toList())
        );
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }
}
