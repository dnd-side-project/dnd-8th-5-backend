package com.dnd.modutime.core.timeblock.application;

import com.dnd.modutime.core.timeblock.application.command.TimeReplaceCommand;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequestV1;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.repository.AvailableDateTimeRepository;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.core.timeblock.util.DateTimeToAvailableDateTimeConvertorFactory;
import com.dnd.modutime.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        replaceAvailableDateTimes(roomUuid, timeReplaceRequest.getName(),
                timeReplaceRequest.getHasTime(), timeReplaceRequest.getAvailableDateTimes());
    }

    public void replaceV1(TimeReplaceCommand command) {
        replaceAvailableDateTimes(command.getRoomUuid(), command.getParticipantName(),
                command.getHasTime(), command.getAvailableDateTimes());
    }

    /**
     * 참여자의 가능한 시간을 교체한다.
     *
     * <p>기존엔 매 요청마다 전량 삭제 후 전량 재삽입(delete-all + insert-all)했으나,
     * 이는 락 보유시간을 늘리고 동시 요청 시 StaleStateException(이미 삭제된 row 재삭제)을 유발했다.
     * 이제 기존값과 신규값을 (날짜 + 시간 집합) 단위로 비교해 <b>삭제분만 delete / 추가분만 insert</b>하고,
     * 변경이 전혀 없으면 DB 연산·이벤트 발행·TimeTable 재계산을 모두 건너뛴다.</p>
     *
     * <p>TimeTable 동기화용 이벤트({@link com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent})에는
     * 기존 전체 / 신규 전체가 그대로 실려야 하므로, timeBlock 컬렉션은
     * 유지분(기존 영속객체) + 추가분(신규객체)으로 재구성한다.</p>
     */
    private void replaceAvailableDateTimes(String roomUuid, String participantName,
                                           Boolean hasTime, List<LocalDateTime> dateTimes) {
        var timeBlock = getTimeBlockByRoomUuidAndParticipantName(roomUuid, participantName);

        var convertor = dateTimeToAvailableDateTimeConvertorFactory.getInstance(hasTime);
        var newAvailableDateTimes = convertor.convert(timeBlock, dateTimes);

        timeReplaceValidator.validate(roomUuid, newAvailableDateTimes);

        var oldAvailableDateTimes = availableDateTimeRepository.findByTimeBlockId(timeBlock.getId());
        var toDelete = oldAvailableDateTimes.stream()
                .filter(old -> newAvailableDateTimes.stream().noneMatch(now -> isSameSlot(old, now)))
                .collect(Collectors.toList());
        var toAdd = newAvailableDateTimes.stream()
                .filter(now -> oldAvailableDateTimes.stream().noneMatch(old -> isSameSlot(old, now)))
                .collect(Collectors.toList());

        if (toDelete.isEmpty() && toAdd.isEmpty()) {
            return;
        }

        availableDateTimeRepository.deleteAll(toDelete);
        availableDateTimeRepository.saveAll(toAdd);

        var finalAvailableDateTimes = new ArrayList<AvailableDateTime>();
        oldAvailableDateTimes.stream()
                .filter(old -> newAvailableDateTimes.stream().anyMatch(now -> isSameSlot(old, now)))
                .forEach(finalAvailableDateTimes::add);
        finalAvailableDateTimes.addAll(toAdd);

        timeBlock.replace(finalAvailableDateTimes);
        timeBlockRepository.save(timeBlock);
    }

    /**
     * 같은 슬롯(날짜 + 시간 집합)인지 비교한다. 날짜만 모드는 times가 null 또는 빈 리스트다.
     */
    private boolean isSameSlot(AvailableDateTime a, AvailableDateTime b) {
        if (!a.getDate().isEqual(b.getDate())) {
            return false;
        }
        return isSameTimes(a.getTimesOrNull(), b.getTimesOrNull());
    }

    private boolean isSameTimes(List<AvailableTime> a, List<AvailableTime> b) {
        var aEmpty = (a == null || a.isEmpty());
        var bEmpty = (b == null || b.isEmpty());
        if (aEmpty || bEmpty) {
            return aEmpty && bEmpty;
        }
        if (a.size() != b.size()) {
            return false;
        }
        var aTimes = a.stream().map(AvailableTime::getTime).collect(Collectors.toSet());
        var bTimes = b.stream().map(AvailableTime::getTime).collect(Collectors.toSet());
        return aTimes.equals(bTimes);
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
