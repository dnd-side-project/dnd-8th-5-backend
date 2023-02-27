package com.dnd.modutime.timetable.repository;

import com.dnd.modutime.timetable.domain.TimeTable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    Optional<TimeTable> findByRoomUuid(String roomUuid);
}
