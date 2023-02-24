package com.dnd.modutime.domain;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import com.dnd.modutime.domain.timeblock.AvailableTime;
import com.dnd.modutime.domain.timeblock.TimeBlock;
import java.util.List;
import org.junit.jupiter.api.Test;

class AvailableDateTimeTest {

    @Test
    void AvailableDateTime생성시_date가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new AvailableDateTime(null, null, List.of(new AvailableTime(_12_00))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void times가_null이면_꺼낼때_null을_반환한다() {
        AvailableDateTime availableDateTime = new AvailableDateTime(null, _2023_02_10, null);
        assertThat(availableDateTime.getTimesOrNull()).isNull();
    }

    @Test
    void time이_null_이면_가지고있지_않다고_판단한다() {
        AvailableDateTime availableDateTime = new AvailableDateTime(new TimeBlock(ROOM_UUID, "참여자1"), _2023_02_10, null);
        assertThat(availableDateTime.hasTime()).isFalse();
    }
}