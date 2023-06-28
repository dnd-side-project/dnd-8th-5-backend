package com.dnd.modutime.core.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.modutime.core.timetable.domain.TimeInfoParticipantName;

public interface TimeInfoParticipantNameRepository extends JpaRepository<TimeInfoParticipantName, Long> {

    void deleteByTimeInfoIdAndName(Long timeInfoId, String name);
}
