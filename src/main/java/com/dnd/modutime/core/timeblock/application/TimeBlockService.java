package com.dnd.modutime.core.timeblock.application;

import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.repository.AvailableDateTimeRepository;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.core.timeblock.util.DateTimeToAvailableDateTimeConvertorFactory;
import com.dnd.modutime.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TimeBlockService {

    private final TimeReplaceValidator timeReplaceValidator;
    private final TimeBlockRepository timeBlockRepository;
    private final AvailableDateTimeRepository availableDateTimeRepository;
    private final DateTimeToAvailableDateTimeConvertorFactory dateTimeToAvailableDateTimeConvertorFactory;

    public void create(String roomUuid, String participantName) {
        var timeBlock = new TimeBlock(roomUuid, participantName);
        timeBlockRepository.save(timeBlock);
    }

    public void replace(String roomUuid, TimeReplaceRequest timeReplaceRequest) {
        var timeBlock = getTimeBlockByRoomUuidAndParticipantName(roomUuid, timeReplaceRequest.getName());

        var dateTimeToAvailableDateTimeConvertor = dateTimeToAvailableDateTimeConvertorFactory
                .getInstance(timeReplaceRequest.getHasTime());
        var availableDateTimes = dateTimeToAvailableDateTimeConvertor.convert(timeBlock, timeReplaceRequest.getAvailableDateTimes());

        timeReplaceValidator.validate(roomUuid, availableDateTimes);
        availableDateTimeRepository.deleteAllByTimeBlockId(timeBlock.getId());
        availableDateTimeRepository.saveAll(availableDateTimes);
        timeBlock.replace(availableDateTimes);
        timeBlockRepository.save(timeBlock);
    }

    public void remove(String roomUuid, String participantName) {
        var timeBlock = getTimeBlockByRoomUuidAndParticipantName(roomUuid, participantName);
        availableDateTimeRepository.deleteAllByTimeBlockId(timeBlock.getId());
        timeBlockRepository.delete(timeBlock);
    }

    private TimeBlock getTimeBlockByRoomUuidAndParticipantName(String roomUuid, String participantName) {
        return timeBlockRepository.findByRoomUuidAndParticipantName(roomUuid, participantName)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다."));
    }

    public TimeBlockResponse getTimeBlock(String roomUuid, String name) {
        validateRoomExist(roomUuid);
        return timeBlockRepository.findByRoomUuidAndParticipantName(roomUuid, name)
                .map(timeBlock -> TimeBlockResponse.of(timeBlock.getParticipantName(),
                        availableDateTimeRepository.findByTimeBlockId(timeBlock.getId())))
                .orElse(TimeBlockResponse.of(name, List.of()));
    }

    private void validateRoomExist(String roomUuid) {
        if (!timeBlockRepository.existsByRoomUuid(roomUuid)) {
            throw new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다.");
        }
    }
}
