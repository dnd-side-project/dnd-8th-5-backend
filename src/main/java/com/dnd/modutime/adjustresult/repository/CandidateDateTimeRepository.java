package com.dnd.modutime.adjustresult.repository;

import com.dnd.modutime.adjustresult.domain.CandidateDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateDateTimeRepository extends JpaRepository<CandidateDateTime, Long> {
    void deleteAllByAdjustmentResultId(Long adjustmentResultId);
}
