package com.dnd.modutime.core.adjustresult.integration;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.adjustresult.application.AdjustmentResultReplaceService;
import com.dnd.modutime.core.adjustresult.application.command.AdjustmentResultReplaceCommand;
import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.adjustresult.repository.CandidateDateTimeRepository;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfoParticipantName;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static com.dnd.modutime.fixture.TimeTableFixture.getDateInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@SpringBootTestWithoutOAuthConfig
@Transactional
public class AdjustmentResultReplaceServiceTest extends IntegrationSupporter {

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
        var adjustmentResult = new AdjustmentResult(ROOM_UUID, List.of());
        given(adjustmentResultRepository.findByRoomUuid(ROOM_UUID)).willReturn(Optional.of(adjustmentResult));
        var savedAdjustmentResult = adjustmentResultRepository.save(adjustmentResult);
        candidateDateTimeRepository.save(
                new CandidateDateTime(
                        savedAdjustmentResult,
                        _2023_02_09_00_00,
                        _2023_02_10_00_00,
                        true,
                        List.of(new CandidateDateTimeParticipantName("김동호"), new CandidateDateTimeParticipantName("이수진")))
        );

        adjustmentResultReplaceService.replace(AdjustmentResultReplaceCommand.of(ROOM_UUID, List.of(getDateInfo(_2023_02_09,
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
    }

    private TimeInfo getTimeInfo(LocalTime time) {
        return new TimeInfo(time, List.of(new TimeInfoParticipantName(null, "김동호")));
    }
}
