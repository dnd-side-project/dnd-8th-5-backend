package com.dnd.modutime.core.timeblock.repository;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableDateTimeRepository extends JpaRepository<AvailableDateTime, Long> {

    void deleteAllByTimeBlockId(Long timeBlockId);

    List<AvailableDateTime> findByTimeBlockId(Long timeBlockId);
}
