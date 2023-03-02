package com.dnd.modutime.adjustresult.integration;

import com.dnd.modutime.adjustresult.application.AdjustmentResultReplaceService;
import com.dnd.modutime.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.adjustresult.repository.CandidateDateTimeRepository;
import com.dnd.modutime.timetable.domain.TimeInfo;
import com.dnd.modutime.timetable.domain.TimeInfoParticipantName;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AdjustmentResultReplaceServiceTest {

    @SpyBean
    private AdjustmentResultRepository adjustmentResultRepository;

    @Autowired
    private CandidateDateTimeRepository candidateDateTimeRepository;

    @Autowired
    private AdjustmentResultReplaceService adjustmentResultReplaceService;

    // replace의 전파레벨이 requires_new 라서 테스트에서 @Transactional 달면 replace 내부에서 조회시 값이 안들어가있음.
    // @Transactional 안달면 조회후 테스트를 못함.
    @Test
    @Disabled
    void 기존의_후보시간은_삭제하고_새로운_후보시간이_생성된다() {
        /*
        given(adjustmentResultRepository.findByRoomUuid(ROOM_UUID)).willReturn(new AdjustmentResult(1L, ROOM_UUID, List.of()));
        AdjustmentResult adjustmentResult = adjustmentResultRepository.save(new AdjustmentResult(ROOM_UUID, List.of()));
        candidateDateTimeRepository.save(
                new CandidateDateTime(adjustmentResult, _2023_02_10, _11_00, _11_30, true, List.of(
                        new CandidateDateTimeParticipantName("김동호"), new CandidateDateTimeParticipantName("이수진"))));

        adjustmentResultReplaceService.replace(new TimeTableReplaceEvent(ROOM_UUID, List.of(getDateInfo(_2023_02_09,
                List.of(getTimeInfo(_12_00), getTimeInfo(_13_00))
        ))));

        List<CandidateDateTime> actual = adjustmentResultRepository.findByRoomUuid(ROOM_UUID).get()
                .getCandidateDateTimes();
        CandidateDateTime candidateDateTime1 = actual.get(0);
        CandidateDateTime candidateDateTime2 = actual.get(1);
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(candidateDateTime1.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(1)
                        .contains("김동호"),
                () -> assertThat(candidateDateTime2.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(1)
                        .contains("김동호")
        );
        */
    }

    private TimeInfo getTimeInfo(LocalTime time) {
        return new TimeInfo(time, List.of(new TimeInfoParticipantName(null, "김동호")));
    }
}
