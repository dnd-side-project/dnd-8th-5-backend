package com.dnd.modutime.room.repository;

import com.dnd.modutime.room.domain.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByUuid(String uuid);
}
