package com.dnd.modutime.acceptance;

import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.participant.controller.dto.ParticipantsDeleteRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.RoomInfoResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantAcceptanceTest extends AcceptanceSupporter {

    private static final String PARTICIPANT_NAME = "참여자1";
    private static final String EMAIL = "dongho1088@gmail.com";

    @Test
    void 이메일을_등록한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String roomUuid = roomCreationResponse.getUuid();
        로그인_참여자_1234(roomUuid, PARTICIPANT_NAME);
        시간을_등록한다(roomUuid, PARTICIPANT_NAME, true,
                List.of(LocalDateTime.of(_2023_02_09, _11_00),
                        LocalDateTime.of(_2023_02_09, _12_00))

        );
        ExtractableResponse<Response> response = post("/api/room/" + roomUuid + "/email", new EmailCreationRequest(
                PARTICIPANT_NAME, EMAIL
        ));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 이메일을_조회한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String roomUuid = roomCreationResponse.getUuid();
        로그인_참여자_1234(roomUuid, PARTICIPANT_NAME);
        시간을_등록한다(roomUuid, PARTICIPANT_NAME, true,
                List.of(LocalDateTime.of(_2023_02_09, _11_00),
                        LocalDateTime.of(_2023_02_09, _12_00))
        );
        post("/api/room/" + roomUuid + "/email", new EmailCreationRequest(PARTICIPANT_NAME, EMAIL));

        EmailResponse emailResponse = 이메일을_조회한다(roomUuid, PARTICIPANT_NAME);
        assertThat(emailResponse.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void 이메일을_조회한다_없으면_null을_반환한다() {
        RoomCreationResponse roomCreationResponse = 방_생성();
        String roomUuid = roomCreationResponse.getUuid();
        로그인_참여자_1234(roomUuid, PARTICIPANT_NAME);
        시간을_등록한다(roomUuid, PARTICIPANT_NAME, false,
                List.of(LocalDateTime.of(_2023_02_09, _11_00),
                        LocalDateTime.of(_2023_02_09, _12_00))
        );

        EmailResponse emailResponse = 이메일을_조회한다(roomUuid, PARTICIPANT_NAME);
        assertThat(emailResponse.getEmail()).isNull();
    }

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
        var request = new ParticipantsDeleteRequest(List.of("이채민", "김주현"));
        var response = 참여자를_삭제한다(roomUuid, request);

        ExtractableResponse<Response> roomInfoResponse = get("/api/room/" + roomCreationResponse.getUuid());
        RoomInfoResponse roomInfo = roomInfoResponse.body().as(RoomInfoResponse.class);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(roomInfo.getParticipantNames()).hasSize(1).containsExactly("김동호");
        });
    }
}
