package com.dnd.modutime.core.room.repository;

import com.dnd.modutime.core.room.domain.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByUuid(String uuid);
}
