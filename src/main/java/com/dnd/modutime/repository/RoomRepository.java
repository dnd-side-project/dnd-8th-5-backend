package com.dnd.modutime.repository;

import com.dnd.modutime.domain.room.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByUuid(String uuid);
}
