package com.dnd.modutime.acceptance;

import com.dnd.modutime.dto.request.ParticipantRequest;
import com.dnd.modutime.dto.response.RoomResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantAcceptanceTest extends AcceptanceSupporter{

    @Test
    void 방에_참여자를_추가한다() {
        RoomResponse roomResponse = 방_생성();

        ParticipantRequest participantRequest = getParticipantRequest("참여자1", "participant1@email.com");
        ExtractableResponse<Response> response1 = post("/api/room/" + roomResponse.getUuid() + "/participant", participantRequest);
        assertThat(response1.statusCode()).isEqualTo(200);
    }

    private ParticipantRequest getParticipantRequest(String name, String email) {
        return new ParticipantRequest(name, email);
    }
}
