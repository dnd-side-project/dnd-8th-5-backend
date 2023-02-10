package com.dnd.modutime.util;

import static com.dnd.modutime.fixture.TimeFixture._2023_02_10_00_00;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dnd.modutime.domain.FakeTimeProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TimerTest {

    @Test
    void 현재시간에_day_hour_minute_을_더한값을_반환한다() {
        FakeTimeProvider timeProvider = new FakeTimeProvider();
        timeProvider.setTime(_2023_02_10_00_00);
        LocalDateTime deadLine = Timer.calculateDeadLine(2, 10, 30, timeProvider);
        assertThat(deadLine).isEqualTo(LocalDateTime.of(2023, 2, 12, 10, 30));
    }

    @Test
    void day_값은_음수가_들어오면_예외를_반환한다() {
        FakeTimeProvider timeProvider = new FakeTimeProvider();
        timeProvider.setTime(_2023_02_10_00_00);
        assertThatThrownBy(() -> Timer.calculateDeadLine(-2, 10, 30, timeProvider))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void hour_값은_음수가_들어오면_예외를_반환한다() {
        FakeTimeProvider timeProvider = new FakeTimeProvider();
        timeProvider.setTime(_2023_02_10_00_00);
        assertThatThrownBy(() -> Timer.calculateDeadLine(2, -10, 30, timeProvider))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void minute_값은_음수가_들어오면_예외를_반환한다() {
        FakeTimeProvider timeProvider = new FakeTimeProvider();
        timeProvider.setTime(_2023_02_10_00_00);
        assertThatThrownBy(() -> Timer.calculateDeadLine(2, 10, -30, timeProvider))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
