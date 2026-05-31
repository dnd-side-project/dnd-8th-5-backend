package com.dnd.modutime.core.timetable.repository;

import com.dnd.modutime.core.timetable.domain.TimeTable;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    Optional<TimeTable> findByRoomUuid(String roomUuid);

    /**
     * 쓰기 경로(이벤트 핸들러발 update) 전용 조회.
     * roomUuid가 unique 인덱스이므로 정확히 1 row에만 비관적 쓰기 락(SELECT ... FOR UPDATE)을 건다.
     * 같은 방의 동시 수정을 방 단위로 직렬화하여 time_info / time_info_participant_name 갱신 시
     * 발생하던 데드락(CannotAcquireLockException)을 원천 제거한다.
     * 읽기(조회 API)는 락 없는 {@link #findByRoomUuid(String)}를 사용한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TimeTable t where t.roomUuid = :roomUuid")
    Optional<TimeTable> findByRoomUuidForUpdate(@Param("roomUuid") String roomUuid);
}
