package com.dnd.modutime.repository;

import com.dnd.modutime.domain.timetable.TimeTable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    Optional<TimeTable> findByRoomUuid(String roomUuid);
}
