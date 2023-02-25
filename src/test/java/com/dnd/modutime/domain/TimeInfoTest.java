package com.dnd.modutime.domain;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dnd.modutime.domain.timetable.TimeInfo;
import org.junit.jupiter.api.Test;

public class TimeInfoTest {

    @Test
    void time이_같으면_count를_감소시킨다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, 1);
        timeInfo.minusCountIfSameTime(_12_00);
        assertThat(timeInfo.getCount()).isEqualTo(0);
    }

    @Test
    void count가_음수가되면_예외를_반환한다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, 0);
        assertThatThrownBy(timeInfo::minusCount)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void time이_같지_않으면_count를_감소시키지_않는다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, 1);
        timeInfo.minusCountIfSameTime(_13_00);
        assertThat(timeInfo.getCount()).isEqualTo(1);
    }

    @Test
    void time이_같으면_count를_증가시킨다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, 1);
        timeInfo.plusCountIfSameTime(_12_00);
        assertThat(timeInfo.getCount()).isEqualTo(2);
    }

    @Test
    void time이_같지_않으면_count를_증가시키지_않는다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, 1);
        timeInfo.plusCountIfSameTime(_13_00);
        assertThat(timeInfo.getCount()).isEqualTo(1);
    }
}
