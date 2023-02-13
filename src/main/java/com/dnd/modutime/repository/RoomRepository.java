package com.dnd.modutime.repository;

import com.dnd.modutime.domain.Room;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepository {

    private Long id = 0L;
    private final Map<Long, Room> store = new HashMap<>();

    public Long save(Room room) {
        store.put(id, room);
        return id++;
    }

    public Room findByUuid(String roomUuid) {
        return store.values().stream()
                .filter(room -> room.getUuid().equals(roomUuid))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 uuid를 가진 room이 없습니다."));
    }
}
