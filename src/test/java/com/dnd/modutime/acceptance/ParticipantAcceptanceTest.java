package com.dnd.modutime.acceptance;

import com.dnd.modutime.core.participant.controller.dto.ParticipantsDeleteRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.V2RoomInfoResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static com.dnd.modutime.fixture.TimeFixture.*;

public class ParticipantAcceptanceTest extends AcceptanceSupporter {

    private static final String PARTICIPANT_NAME = "참여자1";

    @DisplayName("참여자를 삭제한다.")
    @Test
    void test01() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String roomUuid = roomCreationResponse.getUuid();
        로그인_참여자_1234(roomUuid, "이채민");
        로그인_참여자_1234(roomUuid, "김주현");
        로그인_참여자_1234(roomUuid, "김동호");
        시간을_등록한다(roomUuid, PARTICIPANT_NAME, false,
                List.of(LocalDateTime.of(_2023_02_09, _11_00),
                        LocalDateTime.of(_2023_02_09, _12_00))
        );
        var roomInfo1 = get("/api/v2/room/" + roomCreationResponse.getUuid())
                .body()
                .as(V2RoomInfoResponse.class);
        var target1 = roomInfo1.getParticipants().get(0).id();
        var target2 = roomInfo1.getParticipants().get(1).id();
        var request = new ParticipantsDeleteRequest(List.of(target1, target2));
        var response = 참여자를_삭제한다(roomUuid, request);

        ExtractableResponse<Response> roomInfoResponse = get("/api/v2/room/" + roomCreationResponse.getUuid());
        var roomInfo = roomInfoResponse.body().as(V2RoomInfoResponse.class);

        var remain = roomInfo1.getParticipants().get(2);
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(roomInfo.getParticipants()).hasSize(1).containsExactly(remain);
        });
    }
}
