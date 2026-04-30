package com.dnd.modutime.core.timeblock.repository;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailableDateTimeRepository extends JpaRepository<AvailableDateTime, Long> {

    /*
     * cascade = REMOVE 는 JPQL bulk delete 에서 발동되지 않으므로, 자식 테이블(available_time)을
     * 먼저 비우고 부모 테이블을 비운다. derived deleteAllByTimeBlockId 는 ID 단위 DELETE 의
     * row count 검증 때문에 동시 요청 시 StaleStateException 을 일으킨다 (MODUTIME-8).
     */

    @Modifying(flushAutomatically = true)
    @Query(value = "delete from available_time where available_date_time_id in " +
            "(select id from available_date_time where time_block_id = :timeBlockId)",
            nativeQuery = true)
    void deleteAllAvailableTimeByTimeBlockId(@Param("timeBlockId") Long timeBlockId);

    @Modifying(flushAutomatically = true)
    @Query("delete from AvailableDateTime adt where adt.timeBlock.id = :timeBlockId")
    void deleteAllAvailableDateTimeByTimeBlockId(@Param("timeBlockId") Long timeBlockId);

    default void deleteAllByTimeBlockId(Long timeBlockId) {
        deleteAllAvailableTimeByTimeBlockId(timeBlockId);
        deleteAllAvailableDateTimeByTimeBlockId(timeBlockId);
    }

    List<AvailableDateTime> findByTimeBlockId(Long timeBlockId);
}
