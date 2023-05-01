package com.dnd.modutime.core.room.util;

import com.dnd.modutime.core.adjustresult.util.convertor.CandidateDateTimeConvertor;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.exception.NotFoundException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandidateDateTimeConvertorFactory {

    private final RoomRepository roomRepository;
    private final Map<String, CandidateDateTimeConvertor> convertors;

    public CandidateDateTimeConvertor getInstance(String roomUuid) {
        Room room = getByUuid(roomUuid);
        if (room.hasStartAndEndTime()) {
            return convertors.get("dateTimeRoomConvertor");
        }
        return convertors.get("dateRoomConvertor");
    }

    private Room getByUuid(String roomUuid) {
        return roomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
