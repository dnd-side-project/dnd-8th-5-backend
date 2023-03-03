package com.dnd.modutime.timeblock.application;

import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.dto.response.TimeBlockResponse;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.timeblock.domain.TimeBlock;
import com.dnd.modutime.timeblock.repository.AvailableDateTimeRepository;
import com.dnd.modutime.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.timeblock.util.DateTimeToAvailableDateTimeConvertor;
import com.dnd.modutime.timeblock.util.DateTimeToAvailableDateTimeConvertorFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TimeBlockService {

    private final TimeReplaceValidator timeReplaceValidator;
    private final TimeBlockRepository timeBlockRepository;
    private final AvailableDateTimeRepository availableDateTimeRepository;
    private final DateTimeToAvailableDateTimeConvertorFactory dateTimeToAvailableDateTimeConvertorFactory;

    public void replace(String roomUuid, TimeReplaceRequest timeReplaceRequest) {
        TimeBlock timeBlock = getTimeBlockByRoomUuidAndParticipantName(roomUuid, timeReplaceRequest.getName());

        DateTimeToAvailableDateTimeConvertor dateTimeToAvailableDateTimeConvertor = dateTimeToAvailableDateTimeConvertorFactory
                .getInstance(timeReplaceRequest.getHasTime());
        List<AvailableDateTime> availableDateTimes = dateTimeToAvailableDateTimeConvertor.convert(timeBlock, timeReplaceRequest.getAvailableDateTimes());

        timeReplaceValidator.validate(roomUuid, availableDateTimes);
        availableDateTimeRepository.deleteAllByTimeBlockId(timeBlock.getId());
        availableDateTimeRepository.saveAll(availableDateTimes);
        timeBlock.replace(availableDateTimes);
        timeBlockRepository.save(timeBlock);
    }

    private TimeBlock getTimeBlockByRoomUuidAndParticipantName(String roomUuid, String participantName) {
        return timeBlockRepository.findByRoomUuidAndParticipantName(roomUuid, participantName)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다."));
    }

    public TimeBlockResponse getTimeBlock(String roomUuid, String name) {
        TimeBlock timeBlock = getTimeBlockByRoomUuidAndParticipantName(roomUuid, name);
        List<AvailableDateTime> availableDateTimes = availableDateTimeRepository.findByTimeBlockId(timeBlock.getId());
        return TimeBlockResponse.of(timeBlock.getParticipantName(), availableDateTimes);
    }
}
