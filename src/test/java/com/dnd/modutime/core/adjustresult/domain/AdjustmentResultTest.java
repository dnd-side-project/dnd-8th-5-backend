package com.dnd.modutime.core.adjustresult.domain;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdjustmentResultTest {

    @Test
    void 생성시_확정상태_false_로_생성된다() {
        AdjustmentResult adjustmentResult = new AdjustmentResult(ROOM_UUID, List.of());
        assertThat(adjustmentResult.isConfirmation()).isFalse();
    }
}