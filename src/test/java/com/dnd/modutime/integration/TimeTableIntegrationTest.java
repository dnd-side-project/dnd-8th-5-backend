package com.dnd.modutime.integration;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.dnd.modutime.application.TimeReplaceValidator;
import com.dnd.modutime.application.TimeTableService;
import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import com.dnd.modutime.domain.timeblock.AvailableTime;
import com.dnd.modutime.domain.timeblock.TimeBlock;
import com.dnd.modutime.dto.request.AvailableDateTimeRequest;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.repository.AvailableDateTimeRepository;
import com.dnd.modutime.repository.TimeBlockRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class TimeTableIntegrationTest {

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Autowired
    private AvailableDateTimeRepository availableDateTimeRepository;

    @MockBean
    private TimeReplaceValidator timeReplaceValidator;

    @Test
    void 참여자가_가능한_시간을_교체하면_새로운_시간이_등록된다() {
        // given
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeBlock savedTimeBlock = timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));

        // when
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));
        timeTableService.replace(ROOM_UUID, timeReplaceRequest);

        // then
        TimeBlock timeBlock = timeBlockRepository.findById(savedTimeBlock.getId()).get();
        AvailableDateTime availableDateTime = timeBlock.getAvailableDateTimes().get(0);
        assertAll(
                () -> assertThat(availableDateTime.getDate()).isEqualTo(_2023_02_10),
                () -> assertThat(availableDateTime.getTimesOrNull().stream()
                        .map(AvailableTime::getTime)
                        .collect(Collectors.toList())).hasSize(2)
                        .contains(_12_00, _13_00)
        );
    }

    @Test
    void 시작과_끝나는_시간이_없는_방에_시간값을_넘겨주지_않으면_날짜만_저장된다() {
        // given
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeBlock savedTimeBlock = timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));

        // when
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, null)));
        timeTableService.replace(ROOM_UUID, timeReplaceRequest);

        // then
        TimeBlock timeBlock = timeBlockRepository.findById(savedTimeBlock.getId()).get();
        AvailableDateTime availableDateTime = timeBlock.getAvailableDateTimes().get(0);
        assertAll(
                () -> assertThat(availableDateTime.getDate()).isEqualTo(_2023_02_10),
                () -> assertThat(availableDateTime.getTimesOrNull()).isNull()
        );
    }

    @Test
    void 참여자가_가능한_시간을_교체하면_원래_가지고있던_시간은_삭제되어야한다() {
        // given
        doNothing().when(timeReplaceValidator).validate(any(), any());
        TimeBlock savedTimeBlock = timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));
        AvailableDateTime savedAvailableDateTime = availableDateTimeRepository.save(
                new AvailableDateTime(savedTimeBlock, _2023_02_09, List.of(new AvailableTime(_12_00))));

        // when
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));
        timeTableService.replace(ROOM_UUID, timeReplaceRequest);

        // then
        Optional<AvailableDateTime> actual = availableDateTimeRepository.findById(savedAvailableDateTime.getId());
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void 참여자가_가능한_시간을_등록시_validator에서_예외가_발생하면_예외가_발생한다() {
        // given
        doThrow(IllegalArgumentException.class).when(timeReplaceValidator).validate(any(), any());
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));

        // when
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", List.of(new AvailableDateTimeRequest(
                _2023_02_10, List.of(_12_00, _13_00))));

        // then
        assertThatThrownBy(() -> timeTableService.replace(ROOM_UUID, timeReplaceRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
