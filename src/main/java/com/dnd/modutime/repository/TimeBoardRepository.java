package com.dnd.modutime.repository;

import com.dnd.modutime.domain.TimeBoard;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TimeBoardRepository {

    private Long id = 0L;
    private final Map<Long, TimeBoard> store = new HashMap<>();

    public Long save(TimeBoard timeBoard) {
        store.put(id, timeBoard);
        return id++;
    }


    public Optional<TimeBoard> findByRoomUuid(String roomUuid) {
        return store.values().stream()
                .filter(timeBoard -> timeBoard.getRoomUuid().equals(roomUuid))
                .findAny();
    }
}
