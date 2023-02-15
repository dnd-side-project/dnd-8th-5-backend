package com.dnd.modutime.domain;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

public class TimeBoardTest {

    @Test
    void TimeBoard는_roomUuid가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new TimeBoard(null, _12_00, _13_00, List.of(_2023_02_10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void TimeBoard는_dates가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new TimeBoard("7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9", _12_00, _13_00, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void TimeBoard는_dates가_비어있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new TimeBoard("7c64aa0e-6e8f-4f61-b8ee-d5a86493d3a9", _12_00, _13_00, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
