package com.dnd.modutime.timetable.repository;

import com.dnd.modutime.timetable.domain.TimeInfoParticipantName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeInfoParticipantNameRepository extends JpaRepository<TimeInfoParticipantName, Long> {

    void deleteByTimeInfoIdAndName(Long timeInfoIds, String name);
}
