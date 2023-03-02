package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.dto.response.AdjustmentResultResponse;
import com.dnd.modutime.dto.response.CandidateDateTimeResponse;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AdjustmentResultAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/adjustment-result");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeRespons = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeRespons.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeRespons).hasSizeLessThan(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getParticipantNames())
                        .hasSize(3)
                        .contains("김동호", "이수진", "이세희"),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNotNull()
        );
    }
}
