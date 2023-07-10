package com.dnd.modutime.core.timeblock.integration;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._00_00;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.repository.AvailableDateTimeRepository;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class TimeBlockIntegrationTest {

    @Autowired
    private TimeBlockService timeBlockService;

    @SpyBean
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
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", true, List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);

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
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", false, List.of(LocalDateTime.of(_2023_02_10, _00_00)));
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);

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
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", true, List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);

        // then
        Optional<AvailableDateTime> actual = availableDateTimeRepository.findById(savedAvailableDateTime.getId());
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void 시간을_등록하지_않은_참여자로_가능한_시간을_조회하면_빈리스트가_반환된다() {
        // given
        given(timeBlockRepository.existsByRoomUuid(ROOM_UUID)).willReturn(true);

        // when
        TimeBlockResponse timeBlockResponse = timeBlockService.getTimeBlock(ROOM_UUID, "참여자1");
        List<LocalDateTime> availableDateTimes = timeBlockResponse.getAvailableDateTimes();

        // then
        assertThat(availableDateTimes).isEmpty();
    }

    @Test
    void 시간을_등록했다가_다시지운_참여자로_가능한_시간을_조회하면_빈리스트가_반환된다() {
        // given
        given(timeBlockRepository.existsByRoomUuid(ROOM_UUID)).willReturn(true);
        doNothing().when(timeReplaceValidator).validate(any(), any());
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));

        // when
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", false, List.of());
        timeBlockService.replace(ROOM_UUID, timeReplaceRequest);
        TimeBlockResponse timeBlockResponse = timeBlockService.getTimeBlock(ROOM_UUID, "참여자1");
        List<LocalDateTime> availableDateTimes = timeBlockResponse.getAvailableDateTimes();

        // then
        assertThat(availableDateTimes).isEmpty();
    }

    @Test
    void 참여자가_가능한_시간을_등록시_validator에서_예외가_발생하면_예외가_발생한다() {
        // given
        doThrow(IllegalArgumentException.class).when(timeReplaceValidator).validate(any(), any());
        timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));

        // when
        TimeReplaceRequest timeReplaceRequest = new TimeReplaceRequest("참여자1", true, List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));

        // then
        assertThatThrownBy(() -> timeBlockService.replace(ROOM_UUID, timeReplaceRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
