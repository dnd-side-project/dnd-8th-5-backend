package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponseV1;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.util.convertor.CandidateDateTimeConvertor;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorter;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorterFactory;
import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.domain.Participants;
import com.dnd.modutime.core.room.util.CandidateDateTimeConvertorFactory;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeTableResponseGenerator implements AdjustmentResultResponseGenerator {

    private final TimeTableRepository timeTableRepository;
    private final CandidateDateTimeConvertorFactory candidateDateTimeConvertorFactory;
    private final CandidateDateTimesSorterFactory candidateDateTimesSorterFactory;
    private final ParticipantQueryService participantQueryService;

    @Override
    public AdjustmentResultResponse generate(String roomUuid,
                                             CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                             List<String> names) {
        TimeTable timeTable = getTimeTableByRoomUuid(roomUuid);
        List<DateTimeInfoDto> dateTimeInfosDto = timeTable.getDateTimeInfosDtoByParticipantNames(names);
        CandidateDateTimeConvertor candidateDateTimeConvertor = candidateDateTimeConvertorFactory.getInstance(roomUuid);
        List<CandidateDateTime> candidateDateTimes = candidateDateTimeConvertor.convert(dateTimeInfosDto);
        CandidateDateTimesSorter candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        var participants = participantQueryService.getByRoomUuid(roomUuid);
        return AdjustmentResultResponse.from(candidateDateTimes.stream()
                        .limit(5)
                        .collect(Collectors.toList()),
                new Participants(participants)
        );
    }

    @Override
    public AdjustmentResultResponseV1 v1generate(String roomUuid,
                                               CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                               List<String> names) {
        var timeTable = getTimeTableByRoomUuid(roomUuid);
        var dateTimeInfosDto = timeTable.getDateTimeInfosDtoByParticipantNames(names);
        var candidateDateTimeConvertor = candidateDateTimeConvertorFactory.getInstance(roomUuid);
        var candidateDateTimes = candidateDateTimeConvertor.convert(dateTimeInfosDto);
        var candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        var participants = participantQueryService.getByRoomUuid(roomUuid);
        return AdjustmentResultResponseV1.of(new ArrayList<>(candidateDateTimes), new Participants(participants));
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }
}
