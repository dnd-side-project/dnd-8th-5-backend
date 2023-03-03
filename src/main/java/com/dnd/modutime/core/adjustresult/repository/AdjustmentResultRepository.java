package com.dnd.modutime.core.adjustresult.repository;

import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdjustmentResultRepository extends JpaRepository<AdjustmentResult, Long> {
    Optional<AdjustmentResult> findByRoomUuid(String roomUuid);
}
