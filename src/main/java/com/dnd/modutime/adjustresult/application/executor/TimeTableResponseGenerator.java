package com.dnd.modutime.adjustresult.application.executor;

import com.dnd.modutime.adjustresult.application.CandidateDateTimeConvertor;
import com.dnd.modutime.adjustresult.application.CandidateDateTimeDto;
import com.dnd.modutime.adjustresult.application.CandidateDateTimeSorter;
import com.dnd.modutime.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.adjustresult.application.SortedBy;
import com.dnd.modutime.dto.response.AdjustmentResultResponse;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeTableResponseGenerator implements AdjustmentResultResponseGenerator {

    private final TimeTableRepository timeTableRepository;
    private final CandidateDateTimeConvertor candidateDateTimeConvertor;
    private final CandidateDateTimeSorter candidateDateTimeSorter;

    @Override
    public AdjustmentResultResponse generate(String roomUuid,
                                             SortedBy sortedBy,
                                             List<String> names) {
        TimeTable timeTable = getTimeTableByRoomUuid(roomUuid);
        final List<DateTimeInfoDto> dateTimeInfosDto = timeTable.getDateTimeInfosDtoByParticipantNames(names);
        final List<CandidateDateTimeDto> candidateDateTimeDtos = candidateDateTimeConvertor.convert(dateTimeInfosDto);

        // List<CandidateDateTimeDto> -> 정렬 해주는애
        candidateDateTimeSorter.sort(candidateDateTimeDtos);

    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다."));
    }
}
