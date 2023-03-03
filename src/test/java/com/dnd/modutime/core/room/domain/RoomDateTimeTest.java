package com.dnd.modutime.core.room.domain;

import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.core.room.domain.RoomDate;
import org.junit.jupiter.api.Test;

public class RoomDateTimeTest {

    @Test
    void 같은_date라면_true를_반환한다() {
        RoomDate roomDate = new RoomDate(_2023_02_10);
        assertThat(roomDate.isSameDate(new RoomDate(_2023_02_10))).isTrue();
    }

    @Test
    void 다른_date라면_false를_반환한다() {
        RoomDate roomDate = new RoomDate(_2023_02_10);
        assertThat(roomDate.isSameDate(new RoomDate(_2023_02_09))).isFalse();
    }
}
