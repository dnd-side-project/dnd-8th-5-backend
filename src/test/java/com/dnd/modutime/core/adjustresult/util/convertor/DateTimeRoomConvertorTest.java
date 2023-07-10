package com.dnd.modutime.core.adjustresult.util.convertor;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._12_30;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._13_30;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DateTimeRoomConvertorTest {

    @Autowired
    private DateTimeRoomConvertor dateTimeRoomConvertor;

    @Test
    void dateInfosDto를_candidateDateTime으로_변환한다() {
        List<DateTimeInfoDto> dateTimeInfosDto = List.of(
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _12_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _12_30), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _13_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _12_30), List.of("이세희")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _13_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _13_30), List.of("김동호", "이수진"))
        );

        List<CandidateDateTime> candidateDateTimes = dateTimeRoomConvertor.convert(dateTimeInfosDto);
        CandidateDateTime candidateDateTimeDto1 = candidateDateTimes.get(0);
        CandidateDateTime candidateDateTimeDto2 = candidateDateTimes.get(1);
        CandidateDateTime candidateDateTimeDto3 = candidateDateTimes.get(2);

        assertAll(
                () -> assertThat(candidateDateTimeDto1.getStartDateTime()).isEqualTo(LocalDateTime.of(_2023_02_09, _12_00)),
                () -> assertThat(candidateDateTimeDto1.getEndDateTime()).isEqualTo(LocalDateTime.of(_2023_02_09, _13_30)),
                () -> assertThat(candidateDateTimeDto1.isConfirmed()).isNull(),
                () -> assertThat(candidateDateTimeDto1.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeDto2.getStartDateTime()).isEqualTo(LocalDateTime.of(_2023_02_10, _12_30)),
                () -> assertThat(candidateDateTimeDto2.getEndDateTime()).isEqualTo(LocalDateTime.of(_2023_02_10, _13_00)),
                () -> assertThat(candidateDateTimeDto2.isConfirmed()).isNull(),
                () -> assertThat(candidateDateTimeDto2.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(1)
                        .contains("이세희"),
                () -> assertThat(candidateDateTimeDto3.getStartDateTime()).isEqualTo(LocalDateTime.of(_2023_02_10, _13_00)),
                () -> assertThat(candidateDateTimeDto3.getEndDateTime()).isEqualTo(LocalDateTime.of(_2023_02_10, _14_00)),
                () -> assertThat(candidateDateTimeDto3.isConfirmed()).isNull(),
                () -> assertThat(candidateDateTimeDto3.getParticipantNames().stream()
                        .map(CandidateDateTimeParticipantName::getName)
                        .collect(Collectors.toList())).hasSize(2)
                        .contains("김동호", "이수진")
        );
    }

    @Test
    void dateInfosDto이_비어있을경우_빈리스트를_반환한다() {
        List<DateTimeInfoDto> dateTimeInfosDto = List.of();
        List<CandidateDateTime> candidateDateTimes = dateTimeRoomConvertor.convert(dateTimeInfosDto);
        assertThat(candidateDateTimes).isEmpty();
    }
}
