package com.dnd.modutime.acceptance;

import com.dnd.modutime.dto.response.RoomResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RoomAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 방을_생성한다() {
        RoomResponse roomResponse = 방_생성();
        assertThat(roomResponse.getUuid()).isNotNull();
    }
}
