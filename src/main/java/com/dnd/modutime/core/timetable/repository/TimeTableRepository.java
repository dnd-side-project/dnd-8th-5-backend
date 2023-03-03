package com.dnd.modutime.core.timetable.repository;

import com.dnd.modutime.core.timetable.domain.TimeTable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    Optional<TimeTable> findByRoomUuid(String roomUuid);
}
