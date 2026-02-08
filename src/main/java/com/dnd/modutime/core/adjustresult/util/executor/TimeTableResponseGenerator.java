package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.Page;
import com.dnd.modutime.core.Pageable;
import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.application.condition.AdjustmentResultSearchCondition;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponseV1;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponseV1;
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
import com.dnd.modutime.infrastructure.PageResponse;
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
        CandidateDateTimesSorter candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(
                candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        var participants = participantQueryService.getByRoomUuid(roomUuid);
        return AdjustmentResultResponse.from(candidateDateTimes.stream()
                        .limit(5)
                        .collect(Collectors.toList()),
                new Participants(participants)
        );
    }

    @Override
    public Page<CandidateDateTimeResponseV1> v1generate(final AdjustmentResultSearchCondition condition,
                                                        final Pageable pageable) {
        var timeTable = getTimeTableByRoomUuid(condition.getRoomUuid());
        var dateTimeInfosDto = timeTable.getDateTimeInfosDtoByParticipantNames(condition.getParticipantNames());
        var candidateDateTimeConvertor = candidateDateTimeConvertorFactory.getInstance(condition.getRoomUuid());
        var candidateDateTimes = candidateDateTimeConvertor.convert(dateTimeInfosDto);
        var candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(
                condition.getSortedStandard());
        candidateDateTimesSorter.sort(candidateDateTimes);
        var participants = participantQueryService.getByRoomUuid(condition.getRoomUuid());
        final List<CandidateDateTime> pagingCandidateDateTimes = candidateDateTimes.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getSize())
                .toList();
        var response = AdjustmentResultResponseV1.of(pagingCandidateDateTimes, new Participants(participants));
        return new PageResponse<>(response.getCandidateDateTimeResponse(), pageable, candidateDateTimes.size());
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }
}
