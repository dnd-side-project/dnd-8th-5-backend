package com.dnd.modutime.core.adjustresult.repository;

import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdjustmentResultRepository extends JpaRepository<AdjustmentResult, Long> {
    Optional<AdjustmentResult> findByRoomUuid(String roomUuid);

    /**
     * 쓰기 경로(이벤트 핸들러발 replace) 전용 조회.
     * roomUuid가 unique 인덱스이므로 정확히 1 row에만 비관적 쓰기 락을 걸어
     * 같은 방의 조율 결과 재계산(candidate_date_time 전량 교체)을 직렬화한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AdjustmentResult a where a.roomUuid = :roomUuid")
    Optional<AdjustmentResult> findByRoomUuidForUpdate(@Param("roomUuid") String roomUuid);
}
