package com.dnd.modutime.acceptance;

import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.core.participant.application.request.EmailCreationRequest;
import com.dnd.modutime.core.participant.application.response.EmailResponse;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
}
