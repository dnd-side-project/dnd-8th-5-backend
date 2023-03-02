package com.dnd.modutime.adjustresult.integration;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._12_30;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._13_30;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.adjustresult.application.CandidateDateTimeConvertor;
import com.dnd.modutime.adjustresult.application.CandidateDateTimeDto;
import com.dnd.modutime.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.timetable.domain.TimeInfo;
import com.dnd.modutime.timetable.domain.TimeTableParticipantName;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CandidateDateTimeConvertorTest {

    @Autowired
    private CandidateDateTimeConvertor candidateDateTimeConvertor;

    @Test
    void dateInfos를_candidateDateTimesDto로_변환한다() {
        final List<DateTimeInfoDto> dateTimeInfosDto = List.of(
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _12_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _12_30), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_09, _13_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _12_30), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _13_00), List.of("김동호", "이수진")),
                new DateTimeInfoDto(LocalDateTime.of(_2023_02_10, _13_30), List.of("김동호", "이수진")));
//        List<CandidateDateTimeDto> candidateDateTimesDto = candidateDateTimeConvertor.convert(List.of(
//                getDateInfo(_2023_02_09, List.of(getTimeInfo(_12_00, List.of("김동호", "이수진")), getTimeInfo(_12_30, List.of("김동호", "이수진")),
//                        getTimeInfo(_13_00, List.of("이세희")))),
//                getDateInfo(_2023_02_10, List.of(getTimeInfo(_12_30, List.of("이세희")), getTimeInfo(_13_00, List.of("김동호", "이수진")),
//                        getTimeInfo(_13_30, List.of("김동호", "이수진")))
//                )));
        List<CandidateDateTimeDto> candidateDateTimesDto = candidateDateTimeConvertor.convert(dateTimeInfosDto);
        CandidateDateTimeDto candidateDateTimeDto1 = candidateDateTimesDto.get(0);
        CandidateDateTimeDto candidateDateTimeDto2 = candidateDateTimesDto.get(1);
        CandidateDateTimeDto candidateDateTimeDto3 = candidateDateTimesDto.get(2);
        CandidateDateTimeDto candidateDateTimeDto4 = candidateDateTimesDto.get(3);
        assertAll(
                () -> assertThat(candidateDateTimeDto1.getDate()).isEqualTo(_2023_02_09),
                () -> assertThat(candidateDateTimeDto1.getStartTime()).isEqualTo(_12_00),
                () -> assertThat(candidateDateTimeDto1.getEndTime()).isEqualTo(_13_00),
                () -> assertThat(candidateDateTimeDto1.getParticipantNames()).hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeDto2.getDate()).isEqualTo(_2023_02_09),
                () -> assertThat(candidateDateTimeDto2.getStartTime()).isEqualTo(_13_00),
                () -> assertThat(candidateDateTimeDto2.getEndTime()).isEqualTo(_13_30),
                () -> assertThat(candidateDateTimeDto2.getParticipantNames()).hasSize(1)
                        .contains("이세희"),
                () -> assertThat(candidateDateTimeDto3.getDate()).isEqualTo(_2023_02_10),
                () -> assertThat(candidateDateTimeDto3.getStartTime()).isEqualTo(_12_30),
                () -> assertThat(candidateDateTimeDto3.getEndTime()).isEqualTo(_13_00),
                () -> assertThat(candidateDateTimeDto3.getParticipantNames()).hasSize(1)
                        .contains("이세희"),
                () -> assertThat(candidateDateTimeDto4.getDate()).isEqualTo(_2023_02_10),
                () -> assertThat(candidateDateTimeDto4.getStartTime()).isEqualTo(_13_00),
                () -> assertThat(candidateDateTimeDto4.getEndTime()).isEqualTo(_14_00),
                () -> assertThat(candidateDateTimeDto4.getParticipantNames()).hasSize(2)
                        .contains("김동호", "이수진")
        );
    }

    private TimeInfo getTimeInfo(LocalTime time, List<String> participantNames) {
        List<TimeTableParticipantName> timeTableParticipantNames = participantNames.stream()
                .map(participantName -> new TimeTableParticipantName(getTimeInfo(), participantName))
                .collect(Collectors.toList());
        return new TimeInfo(time, timeTableParticipantNames);
    }

    private TimeInfo getTimeInfo() {
        return new TimeInfo(null, null);
    }
}
