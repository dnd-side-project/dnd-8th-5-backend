package com.dnd.modutime.core.room.repository;

import com.dnd.modutime.core.room.domain.Room;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByUuid(String uuid);

    long countByCreatedAtAfter(LocalDateTime from);

    @Query("select r.createdAt from Room r where r.createdAt >= :from")
    List<LocalDateTime> findCreatedAtsAfter(@Param("from") LocalDateTime from);
}
