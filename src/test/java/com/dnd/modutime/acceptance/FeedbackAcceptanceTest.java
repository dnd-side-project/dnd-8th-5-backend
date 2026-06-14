package com.dnd.modutime.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FeedbackAcceptanceTest extends AcceptanceSupporter {

    private static final String VALID_BODY = """
            {
              "category": "PRAISE",
              "content": "UI가 깔끔해서 쓰기 편해요!",
              "interview": { "agreed": false },
              "responses": [
                { "question": "좋았던 점을 알려주고 싶어요", "answer": "UI가 깔끔해서 쓰기 편해요!" }
              ],
              "snapshot": {
                "user": null,
                "room": {
                  "roomId": "xyz789", "type": "CALENDAR", "title": "동아리 MT",
                  "participantCount": 12, "headCount": null, "deadLine": null
                },
                "page": { "routeId": "/$roomId/priority", "pathname": "/xyz789/priority", "search": "?participant=김철수" },
                "env": { "userAgent": "Mozilla/5.0", "viewport": { "width": 412, "height": 915 }, "appVersion": "2.0.0" }
              }
            }
            """;

    @DisplayName("비회원이 토큰 없이 피드백을 제출하면 200과 생성 id를 반환한다")
    @Test
    void 비회원_피드백_제출() {
        ExtractableResponse<Response> response = post("/api/v1/feedback", VALID_BODY);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", Long.class)).isNotNull()
        );
    }

    @DisplayName("content가 비어있으면 400을 응답한다")
    @Test
    void content가_비면_400() {
        String body = VALID_BODY.replace("\"UI가 깔끔해서 쓰기 편해요!\",", "\"\",");

        ExtractableResponse<Response> response = post("/api/v1/feedback", body);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isNotBlank()
        );
    }

    @DisplayName("category가 잘못된 값이면 400을 응답한다")
    @Test
    void category가_잘못되면_400() {
        String body = VALID_BODY.replace("\"PRAISE\"", "\"WRONG\"");

        ExtractableResponse<Response> response = post("/api/v1/feedback", body);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
