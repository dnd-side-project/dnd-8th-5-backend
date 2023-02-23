package com.dnd.modutime.repository;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableDateTimeRepository extends JpaRepository<AvailableDateTime, Long> {

    void deleteAllByTimeBlockId(Long timeBlockId);

    List<AvailableDateTime> findByTimeBlockId(Long timeBlockId);
}
