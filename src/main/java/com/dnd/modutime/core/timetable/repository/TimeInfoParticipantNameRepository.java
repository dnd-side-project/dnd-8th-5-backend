package com.dnd.modutime.core.timetable.repository;

import com.dnd.modutime.core.timetable.domain.TimeInfoParticipantName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeInfoParticipantNameRepository extends JpaRepository<TimeInfoParticipantName, Long> {

    void deleteByTimeInfoIdAndName(Long timeInfoIds, String name);
}
