package com.dnd.modutime.integration;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._13_30;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.dnd.modutime.application.ApplicationTimeTableCalculator;
import com.dnd.modutime.application.TimeReplaceValidator;
import com.dnd.modutime.application.TimeBlockService;
import com.dnd.modutime.domain.timeblock.DateTime;
import com.dnd.modutime.domain.timeblock.TimeBlock;
import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.repository.TimeBlockRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class ApplicationTimeTableCalculatorTest {

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Autowired
    private TimeBlockService timeBlockService;

    @Autowired
    private ApplicationTimeTableCalculator timeTableCalculator;

    @MockBean
    private TimeReplaceValidator timeReplaceValidator;

    @Test
    void roomUuid에_해당하는_날짜_시간당_참여자의_수를_계산한다() {
        doNothing().when(timeReplaceValidator).validate(any(), any());
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "김동호"));
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "이수진"));

        // when
        timeBlockService.replace(ROOM_UUID, new TimeReplaceRequest("김동호", List.of(
                new AvailableDateTimeRequest(_2023_02_09, List.of(_12_00, _13_00)),
                new AvailableDateTimeRequest(_2023_02_10, List.of(_12_00, _13_00))
        )));
        timeBlockService.replace(ROOM_UUID, new TimeReplaceRequest("이수진", List.of(
                new AvailableDateTimeRequest(_2023_02_09, List.of(_11_00, _12_00)),
                new AvailableDateTimeRequest(_2023_02_10, List.of(_12_00, _13_30))
        )));

        // then
        Map<DateTime, Integer> countsByDateTime = timeTableCalculator.calculate(ROOM_UUID);
        assertAll(
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_09, _11_00))).isEqualTo(1),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_09, _12_00))).isEqualTo(2),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_09, _13_00))).isEqualTo(1),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_10, _12_00))).isEqualTo(2),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_10, _13_00))).isEqualTo(1),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_10, _13_30))).isEqualTo(1)
        );
    }

    @Test
    void roomUuid에_해당하는_날짜당_참여자의_수를_계산한다() {
        doNothing().when(timeReplaceValidator).validate(any(), any());
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "김동호"));
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "이수진"));

        // when
        timeBlockService.replace(ROOM_UUID, new TimeReplaceRequest("김동호", List.of(
                new AvailableDateTimeRequest(_2023_02_08, null),
                new AvailableDateTimeRequest(_2023_02_09, null)
        )));
        timeBlockService.replace(ROOM_UUID, new TimeReplaceRequest("이수진", List.of(
                new AvailableDateTimeRequest(_2023_02_09, null),
                new AvailableDateTimeRequest(_2023_02_10, null)
        )));

        // then
        Map<DateTime, Integer> countsByDateTime = timeTableCalculator.calculate(ROOM_UUID);
        assertAll(
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_08, null))).isEqualTo(1),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_09, null))).isEqualTo(2),
                () -> assertThat(countsByDateTime.get(DateTime.of(_2023_02_10, null))).isEqualTo(1)
        );
    }
}
