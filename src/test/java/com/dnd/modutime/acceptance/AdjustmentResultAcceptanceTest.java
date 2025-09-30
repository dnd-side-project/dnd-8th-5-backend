package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequest;
import static com.dnd.modutime.fixture.RoomRequestFixture.getRoomRequestNoTime;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponse;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AdjustmentResultAcceptanceTest extends AcceptanceSupporter {

    @Test
    void 전체참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/adjustment-result");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(3)
                        .contains("김동호", "이수진", "이세희"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .isEmpty(),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNotNull()
        );
    }

    @Test
    void 일부참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/adjustment-result?name=김동호,이수진");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .hasSize(1)
                        .contains("이세희"),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNull()
        );
    }

    @Test
    void 날짜만있는_방의_전체참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        두명의_날짜를_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/adjustment-result");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .isEmpty(),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNotNull()
        );
    }

    @Test
    void 날짜만있는_방의_일부참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        두명의_날짜를_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/room/" + roomUuid + "/adjustment-result?name=김동호");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(1)
                        .contains("김동호"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .hasSize(1)
                        .contains("이수진"),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNull()
        );
    }

    @Test
    void v1_전체참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/v1/room/" + roomUuid + "/adjustment-results");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(13),
                () -> assertThat(candidateDateTimeResponse.getId()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(3)
                        .contains("김동호", "이수진", "이세희"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .isEmpty(),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNotNull()
        );
    }

    @Test
    void v1_일부참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequest(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        세명의_날짜와_시간을_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/v1/room/" + roomUuid + "/adjustment-results?name=김동호,이수진");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .hasSize(1)
                        .contains("이세희"),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNull()
        );
    }

    @Test
    void v1_날짜만있는_방의_전체참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        두명의_날짜를_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/v1/room/" + roomUuid + "/adjustment-results");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(2)
                        .contains("김동호", "이수진"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .isEmpty(),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNotNull()
        );
    }

    @Test
    void v1_날짜만있는_방의_일부참여자의_조율결과를_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성(getRoomRequestNoTime(List.of(_2023_02_08, _2023_02_09, _2023_02_10)));
        String roomUuid = roomCreationResponse.getUuid();
        두명의_날짜를_등록한다(roomUuid);

        ExtractableResponse<Response> response = get("/api/v1/room/" + roomUuid + "/adjustment-results?name=김동호");
        AdjustmentResultResponse adjustmentResultResponse = response.body().as(AdjustmentResultResponse.class);
        List<CandidateDateTimeResponse> candidateDateTimeResponses = adjustmentResultResponse.getCandidateDateTimeResponse();
        CandidateDateTimeResponse candidateDateTimeResponse = candidateDateTimeResponses.get(0);
        assertAll(
                () -> assertThat(candidateDateTimeResponses).hasSizeLessThanOrEqualTo(5),
                () -> assertThat(candidateDateTimeResponse.getId()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getDate()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getDayOfWeek()).isNotNull(),
                () -> assertThat(candidateDateTimeResponse.getStartTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getEndTime()).isNull(),
                () -> assertThat(candidateDateTimeResponse.getAvailableParticipantNames())
                        .hasSize(1)
                        .contains("김동호"),
                () -> assertThat(candidateDateTimeResponse.getUnavailableParticipantNames())
                        .hasSize(1)
                        .contains("이수진"),
                () -> assertThat(candidateDateTimeResponse.getIsConfirmed()).isNull()
        );
    }
}
