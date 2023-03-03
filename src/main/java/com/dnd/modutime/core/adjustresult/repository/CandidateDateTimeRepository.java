package com.dnd.modutime.core.adjustresult.repository;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateDateTimeRepository extends JpaRepository<CandidateDateTime, Long> {
    void deleteAllByAdjustmentResultId(Long adjustmentResultId);
}
