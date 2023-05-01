package com.dnd.modutime.core.adjustresult.util.convertor;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DateRoomConvertorTest {

    @Autowired
    private DateRoomConvertor dateRoomConvertor;

    @Test
    void 날짜만있는_방의_dateInfosDto를_candidateDateTime으로_변환한다() {
        List<DateTimeInfoDto> dateTimeInfosDto = List.of(
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _00_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _00_00), List.of("이세희", "김동호"))
        );

        List<CandidateDateTime> candidateDateTimes = dateRoomConvertor.convert(dateTimeInfosDto);
        CandidateDateTime candidateDateTimeDto1 = candidateDateTimes.get(0);
        CandidateDateTime candidateDateTimeDto2 = candidateDateTimes.get(1);

        assertAll(
                () -> assertThat(candidateDateTimeDto1.getStartDateTime()).isEqualTo(LocalDateTime.of(_2023_02_09, _00_00)),
                () -> assertThat(candidateDateTimeDto1.getEndDateTime()).isEqualTo(LocalDateTime.of(_2023_02_09, _00_00)),
                () -> assertThat(candidateDateTimeDto1.isConfirmed()).isNull(),
                () -> assertThat(candidateDateTimeDto1.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeDto2.getStartDateTime()).isEqualTo(LocalDateTime.of(_2023_02_10, _00_00)),
                () -> assertThat(candidateDateTimeDto2.getEndDateTime()).isEqualTo(LocalDateTime.of(_2023_02_10, _00_00)),
                () -> assertThat(candidateDateTimeDto2.isConfirmed()).isNull(),
                () -> assertThat(candidateDateTimeDto2.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(2)
                        .contains("이세희", "김동호")
        );
    }
}
