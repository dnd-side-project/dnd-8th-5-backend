package com.dnd.modutime.repository;

import com.dnd.modutime.domain.room.Room;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepository {

    private Long id = 0L;
    private final Map<Long, Room> store = new HashMap<>();

    public Long save(Room room) {
        store.put(id, room);
        return id++;
    }

    public Optional<Room> findByUuid(String roomUuid) {
        return store.values().stream()
                .filter(room -> room.getUuid().equals(roomUuid))
                .findAny();
    }
}
